package by.javaguru.apigatewayservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Sergey Stol
 * 2024-11-23
 */
@Configuration
public class WebClientConfig {

   @Value("${security.token-uri}")
   private String tokenUri;

   @Bean
   public WebClient webClient() {
      return WebClient.create(tokenUri);
   }
}
