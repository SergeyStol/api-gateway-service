package by.javaguru.apigatewayservice.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergey Stol
 * 2024-11-21
 */
@Service
@RequiredArgsConstructor
public class JwtTokenService {

   @Value("${security.jwks-uri}")
   private String jwksUri;

   @Value("${security.issuer-uri}")
   private String issuerUri;

   private Map<String, PublicKey> keyCache;

   @PostConstruct
   void init() {
      try {
         this.keyCache = fetchJwksKeys();
      } catch (Exception e) {
      }
   }

   public boolean validateToken(String token) {
      try {
         Jws<Claims> claimsJws = Jwts.parser()
           .setSigningKeyResolver(new SigningKeyResolver() {

              @Override
              public PublicKey resolveSigningKey(JwsHeader header, Claims claims) {
                 String kid = header.getKeyId();
                 if (kid == null || !keyCache.containsKey(kid)) {
                    throw new SignatureException("Unknown Key ID (kid): " + kid);
                 }
                 return keyCache.get(kid);
              }

              @Override
              public Key resolveSigningKey(JwsHeader jwsHeader, byte[] bytes) {
                 return null;
              }
           })
           .requireIssuer(issuerUri)
           .build()
           .parseClaimsJws(token);

         // Validate additional claims if necessary
         Claims claims = claimsJws.getBody();
         System.out.println("Token is valid. Claims: " + claims);
         return true;

      } catch (Exception e) {
         System.err.println("Token validation failed: " + e.getMessage());
         return false;
      }
   }

   private Map<String, PublicKey> fetchJwksKeys() throws Exception {
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder()
        .uri(new URI(jwksUri))
        .GET()
        .build();

      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() != 200) {
         throw new IllegalStateException("Failed to fetch JWKS, status code: " + response.statusCode());
      }

      String jwksResponse = response.body();

      ObjectMapper mapper = new ObjectMapper();
      JsonNode keysNode = mapper.readTree(jwksResponse).get("keys");

      if (keysNode == null || !keysNode.isArray()) {
         throw new IllegalStateException("Invalid JWKS response format");
      }

      Map<String, PublicKey> publicKeyMap = new HashMap<>();
      for (JsonNode keyNode : keysNode) {
         String kid = keyNode.get("kid").asText();
         PublicKey publicKey = extractPublicKey(keyNode);
         publicKeyMap.put(kid, publicKey);
      }
      return publicKeyMap;
   }

   private PublicKey extractPublicKey(JsonNode keyNode) {
      try {
         String modulusBase64 = keyNode.get("n").asText();
         String exponentBase64 = keyNode.get("e").asText();

         BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(modulusBase64));
         BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(exponentBase64));

         RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
         KeyFactory factory = KeyFactory.getInstance("RSA");
         return factory.generatePublic(spec);
      } catch (Exception e) {
         throw new RuntimeException("Failed to extract public key from JWKS", e);
      }
   }
}