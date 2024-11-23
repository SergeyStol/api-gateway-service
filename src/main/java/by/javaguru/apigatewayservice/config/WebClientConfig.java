package by.javaguru.apigatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Sergey Stol
 * 2024-11-23
 */
@Configuration
public class WebClientConfig {

   @Bean
   public WebClient webClient() {
      return WebClient.create("http://localhost:9080/realms/javaguru/protocol/openid-connect/token");
   }
}
