package by.javaguru.apigatewayservice.config;

import jakarta.ws.rs.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author Sergey Stol
 * 2024-11-23
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

   @Bean
   public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
      return http
        .authorizeExchange(auth -> auth
          .pathMatchers(HttpMethod.GET, "/actuator/health").permitAll()
          .pathMatchers("/v1.0/auth/**").permitAll()
          .pathMatchers("/v1.0/experiences/**").hasRole("ROLE_ADMIN")
          .pathMatchers("/v1.0/industries/**").hasRole("ROLE_ADMIN")
          .anyExchange().authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2
          .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .build();
   }

   @Bean
   public ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
      var jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
      var jwtAuthenticationConverter = new JwtAuthenticationConverter();
      jwtAuthenticationConverter.setPrincipalClaimName("preferred_username");
      jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
         var authorities = jwtGrantedAuthoritiesConverter.convert(jwt);
         var roles = (List<String>) jwt.getClaimAsMap("realm_access").get("roles");
         List<GrantedAuthority> list = Stream.concat(authorities.stream(),
             roles.stream()
               .map(role -> "ROLE_" + role)
               .map(SimpleGrantedAuthority::new)
               .map(GrantedAuthority.class::cast))
           .toList();
         return list;
      });
      return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
   }
}
