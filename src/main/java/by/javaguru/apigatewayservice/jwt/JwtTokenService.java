package by.javaguru.apigatewayservice.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Password;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Sergey Stol
 * 2024-11-21
 */
@Service
@AllArgsConstructor
public class JwtTokenService {

   private final Password password;

   public boolean verifySignature(String signedToken) {
      try {
         Jwts.parser()
           .decryptWith(password)
           .build()
           .parseEncryptedClaims(signedToken);
         return true;
      } catch (Exception ex) {
         return false;
      }
   }
}