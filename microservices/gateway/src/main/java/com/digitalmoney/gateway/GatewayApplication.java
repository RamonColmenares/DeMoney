package com.digitalmoney.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@SpringBootApplication
public class GatewayApplication {
	@Value("${microservices.accounts}")
	private String accountDNS;
	@Value("${microservices.users}")
	private String usersDNS;

	private final WebClient webClient;

	public GatewayApplication() {
		this.webClient = WebClient.builder().build();
	}

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				// Accounts Service routes
				.route(r -> r.path("/accounts/**")
						.filters(f -> f.filter(tokenValidationFilter(usersDNS + "/users/validate")))
						.uri(accountDNS))
				// Users Service routes
				.route(r -> r.path("/users/**")
						.uri(usersDNS))
				.build();
	}

	public GatewayFilter tokenValidationFilter(String validationUrl) {
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			List<String> authorizationHeaders = request.getHeaders().get(HttpHeaders.AUTHORIZATION);

			if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
				String token = authorizationHeaders.get(0).replace("Bearer ", "");

				// Validate token by sending a request to ms-users validation endpoint
				Mono<Boolean> isValid = webClient.post()
						.uri(validationUrl)
						.bodyValue(token)
						.retrieve()
						.bodyToMono(Boolean.class);

				return isValid.flatMap(valid -> {
					if (valid) {
						return chain.filter(exchange);
					} else {
						// If token is not valid, remove the token from the request
						ServerHttpRequest mutatedRequest = request.mutate().header(HttpHeaders.AUTHORIZATION).build();
						return chain.filter(exchange.mutate().request(mutatedRequest).build());
					}
				});
			}

			return chain.filter(exchange);
		};
	}
}
