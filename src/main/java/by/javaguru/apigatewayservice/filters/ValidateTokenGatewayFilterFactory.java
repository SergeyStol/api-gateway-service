package by.javaguru.apigatewayservice.filters;

import by.javaguru.apigatewayservice.jwt.JwtTokenService;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * @author Sergey Stol
 * 2024-11-21
 */
@Component
public class ValidateTokenGatewayFilterFactory
  extends AbstractGatewayFilterFactory<ValidateTokenGatewayFilterFactory.Config> {
   private final JwtTokenService jwtTokenService;

   public ValidateTokenGatewayFilterFactory(JwtTokenService jwtTokenService) {
      super(Config.class);
      this.jwtTokenService = jwtTokenService;
   }

   @Override
   public GatewayFilter apply(Config config) {
      return (exchange, chain) -> {
         try {
            HttpHeaders headers = exchange.getRequest().getHeaders();
            String token = headers.get("Authorization").get(0);
            if (jwtTokenService.validateToken(token.substring("Bearer ".length()))) {
               return chain.filter(exchange);
            } else {
               exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
               return exchange.getResponse().setComplete();
            }
         } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
         }

      };
   }

   public static class Config {
   }
}
