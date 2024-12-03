package by.javaguru.apigatewayservice.jwt;

/**
 * @author Sergey Stol
 * 2024-12-03
 */
public interface TokenValidator {
   boolean validateToken(String token);
}
