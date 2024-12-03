package by.javaguru.apigatewayservice.keycloak;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakService {
   private final WebClient webClient;
   private final SecurityProperties securityProperties;

   public Mono<Map<String, Object>> getToken(String login, String password) {
      return webClient.post()
          .uri(securityProperties.getTokenUri())
          .body(BodyInserters.fromFormData("grant_type", securityProperties.getGrantType())
            .with("client_id", securityProperties.getClientId())
            .with("client_secret", securityProperties.getClientSecret())
            .with("username", login)
            .with("password", password))
          .retrieve()
          .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
   }
}