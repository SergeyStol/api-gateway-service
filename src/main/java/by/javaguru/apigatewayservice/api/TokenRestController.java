package by.javaguru.apigatewayservice.api;

import by.javaguru.apigatewayservice.keycloak.KeycloakService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
public class TokenRestController {

   private final KeycloakService keycloakService;

   public TokenRestController(KeycloakService keycloakService) {
      this.keycloakService = keycloakService;
   }

   // POST /api/v1/token  -> 200 CREATED
   //                     -> 409 CONFLICT
   @PostMapping("/token")
   @ResponseStatus(HttpStatus.CREATED)
   public Mono<Map<String, Object>> getToken(@RequestBody UserDto userDto) {
      return keycloakService.getToken(userDto.login(), userDto.password());
   }
}
