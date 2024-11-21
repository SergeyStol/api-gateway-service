package by.javaguru.apigatewayservice.jwt;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.Password;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Sergey Stol
 * 2024-10-09
 */
@Configuration
public class JwtConfig {
   @Value("${jwt.password}")
   String password;

   @Bean
   Password getPassword() {
      return Keys.password(password.toCharArray());
   }
}
