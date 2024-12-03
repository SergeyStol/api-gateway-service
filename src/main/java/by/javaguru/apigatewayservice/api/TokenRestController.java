package by.javaguru.apigatewayservice.api;

import by.javaguru.apigatewayservice.keycloak.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author Sergey Stol
 * 2024-11-23
 */
@RestController
@RequestMapping("/v1.0/auth")
@RequiredArgsConstructor
public class TokenRestController {
   private final KeycloakService keycloakService;

   // GET /v1.0/auth/token -> 200 OK
   @GetMapping(path = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
   @ResponseStatus(HttpStatus.OK)
   public Mono<Map<String, Object>> getToken(@ModelAttribute UserDto userDto) {
      return keycloakService.getToken(userDto.login(), userDto.password())
        .map(map -> Map.of("access_token", map.get("access_token")));
   }
}
