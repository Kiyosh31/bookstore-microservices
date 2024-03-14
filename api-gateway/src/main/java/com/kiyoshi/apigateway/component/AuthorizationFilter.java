package com.kiyoshi.apigateway.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;


@Component
@Order(-1)
public class AuthorizationFilter implements GlobalFilter {
    @Autowired
    private RestTemplate restTemplate;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_PREFIX = "Bearer ";

    @Value("#{'${no-auth.paths}'.split(',')}")
    private List<String> NO_AUTH_PATHS;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();

        // Check if the request path requires authentication
        if (requiresAuthentication(path)) {
            HttpHeaders headers = exchange.getRequest().getHeaders();
            String authorizationHeader = headers.getFirst(AUTHORIZATION_HEADER);

            if (authorizationHeader == null || !authorizationHeader.startsWith(AUTHORIZATION_PREFIX)) {
                return missingAuthHeader(exchange);
            }

            // You can add more complex validation logic here if needed
            // auth-service
//            if(!authValidateTokenRequest(authorizationHeader)){
//                return invalidAuthHeader(exchange);
//            }
        }

        return chain.filter(exchange);
    }

    private boolean requiresAuthentication(String path) {
        for (String noAuthPath : NO_AUTH_PATHS) {
            if(path.equals(noAuthPath)) {
                return false;
            }
        }
        return true;
    }

    private Mono<Void> missingAuthHeader(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String responseBody = "{\"message\": \"Missing Authorization header\"}";
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(responseBody.getBytes())));
    }

    private Mono<Void> invalidAuthHeader(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String responseBody = "{\"message\": \"Invalid Authorization header\"}";
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(responseBody.getBytes())));
    }

    private Boolean authValidateTokenRequest(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Boolean> responseEntity = restTemplate.exchange(
                "http://AUTH-SERVICE/api/v1/auth/validateToken",
                HttpMethod.GET,
                entity,
                Boolean.class
        );

        return responseEntity.getBody();
    }
}
