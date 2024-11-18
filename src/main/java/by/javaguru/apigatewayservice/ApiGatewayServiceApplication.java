package by.javaguru.apigatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiGatewayServiceApplication {

   public static void main(String[] args) {
      SpringApplication.run(ApiGatewayServiceApplication.class, args);
   }

   @Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(r -> r.path("/v1.0/experiences/**")
						.filters(f -> f
//							.prefixPath("/api")
							.addResponseHeader("X-Powered-By","DanSON Gateway Service")
						)
						.uri("http://localhost:8081")
				)
				.route(r -> r.path("/v1.0/industries/**")
						.filters(f -> f
//								.prefixPath("/api")
								.addResponseHeader("X-Powered-By","DanSON Gateway Service")
						)
						.uri("http://localhost:8082")
				)
				.build();
	}

}
