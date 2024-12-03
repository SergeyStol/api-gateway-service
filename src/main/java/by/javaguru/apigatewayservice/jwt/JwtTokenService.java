package by.javaguru.apigatewayservice.jwt;

import by.javaguru.apigatewayservice.keycloak.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Locator;
import io.jsonwebtoken.ProtectedHeader;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigInteger;
import java.net.URI;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Stol
 * 2024-11-21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenService implements TokenValidator {
   private final SecurityProperties securityProperties;
   private final RestClient restClient;

   private Map<String, PublicKey> keyCache;
   private Locator<Key> keyLocator;

   @PostConstruct
   void init() {
      try {
         this.keyCache = fetchJwksKeys();
      } catch (Exception e) {
      }
      keyLocator = getKeyLocator();
   }

   @Override
   public boolean validateToken(String token) {
      try {
         log.info("Trying to validate token: {}", token);
         Claims claims = Jwts.parser()
           .keyLocator(keyLocator)
           .requireIssuer(securityProperties.getIssuerUri())
           .build()
           .parseSignedClaims(token)
           .getPayload();
         log.info("Token is valid. Claims: {}", claims);
         return true;
      } catch (Exception e) {
         log.error("Token validation failed. Reason: {}", e.getMessage());
         return false;
      }
   }

   private Locator<Key> getKeyLocator() {
      return (Header header) -> {
         String kid = ((ProtectedHeader) header).getKeyId();
         if (kid == null || !keyCache.containsKey(kid)) {
            throw new IllegalArgumentException("Unknown Key ID (kid): " + kid);
         }
         return keyCache.get(kid);
      };
   }

   private Map<String, PublicKey> fetchJwksKeys() {
      log.info("Trying to fetch public certificates by address: {} ...", securityProperties.getJwksUri());
      CertificatesDto certificateDto = null;
      try {
         certificateDto = restClient
           .get()
           .uri(new URI(securityProperties.getJwksUri()))
           .retrieve()
           .body(CertificatesDto.class);
         log.info("Trying to fetch public certificates --> Success. {}", certificateDto);
      } catch (Exception e) {
         log.error("Trying to fetch public certificates --> Unsuccessfully. Reason: {}", e.getMessage());
         throw new RuntimeException(e);
      }

      if (certificateDto == null) {
         String msg = "Unsuccessful attempt to fetch public certificates";
         log.error(msg);
         throw new RuntimeException(msg);
      }

      Map<String, PublicKey> publicKeyMap = new HashMap<>();
      for (var certificate : certificateDto.keys()) {
         PublicKey publicKey = extractPublicKey(certificate);
         publicKeyMap.put(certificate.kid(), publicKey);
      }
      return publicKeyMap;
   }

   private PublicKey extractPublicKey(CertificateDto certificate) {
      try {
         log.info("Trying to extract public key from public certificate. Public certificate: {}", certificate);
         String modulusBase64 = certificate.n();
         String exponentBase64 = certificate.e();

         BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(modulusBase64));
         BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(exponentBase64));

         RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
         KeyFactory factory = KeyFactory.getInstance("RSA");
         log.info("Trying to extract public key from public certificate --> Success. " +
                  "RSAPublicKeySpec: {}, KeyFactory: {}", spec, factory);
         return factory.generatePublic(spec);
      } catch (Exception e) {
         log.error("Trying to extract public key from public certificate --> Unsuccessfully. " +
                  "Reason: {})", e.getMessage());
         throw new RuntimeException("Failed to extract public key from public certificate", e);
      }
   }

   record CertificateDto(
     String kid,
     String n,
     String e
   ) {
   }

   record CertificatesDto(
     List<CertificateDto> keys
   ) {
   }
}
