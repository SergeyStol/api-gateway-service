package by.javaguru.apigatewayservice.predicates;

import by.javaguru.apigatewayservice.jwt.JwtTokenService;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;

import java.util.function.Predicate;

/**
 * @author Sergey Stol
 * 2024-11-21
 */
public class ValidateTokenRoutePredicateFactory extends AbstractRoutePredicateFactory<ValidateTokenRoutePredicateFactory.Config> {

   private final JwtTokenService jwtTokenService;

   public ValidateTokenRoutePredicateFactory(JwtTokenService jwtTokenService) {
      super(ValidateTokenRoutePredicateFactory.Config.class);
      this.jwtTokenService = jwtTokenService;
   }

   @Override
   public Predicate<ServerWebExchange> apply(ValidateTokenRoutePredicateFactory.Config config) {
      return exchange -> {
            try {
               HttpHeaders headers = exchange.getRequest().getHeaders();
               String token = headers.get("Authorization").get(0);

               return jwtTokenService.validateToken(token.substring("Bearer ".length()));
            } catch (Exception e) {
               exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
               exchange.getResponse().setRawStatusCode(401);
               return false;
            }
      };
   }

   public static class Config {
   }
}
