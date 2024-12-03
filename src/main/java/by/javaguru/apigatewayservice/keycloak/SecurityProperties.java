package by.javaguru.apigatewayservice.keycloak;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.stereotype.Component;

/**
 * @author Sergey Stol
 * 2024-11-27
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
   private String grantType;
   private String tokenUri;
   private String issuerUri;
   private String jwksUri;
   @Name("client.id")
   private String clientId;
   @Name("client.secret")
   private String clientSecret;
}