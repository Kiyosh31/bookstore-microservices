package com.kiyoshi.apigateway.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Component
@Order(-1)
public class AuthorizationFilter implements GlobalFilter {
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
                return unauthorized(exchange);
            }

            // You can add more complex validation logic here if needed
            // auth
        }

        return chain.filter(exchange);
    }

    private boolean requiresAuthentication(String path) {
        for (String noAuthPath : NO_AUTH_PATHS) {
            if(path.startsWith(noAuthPath)) {
                return false;
            }
        }
        return true;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String responseBody = "{\"message\": \"Missing or invalid Authorization header\"}";
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(responseBody.getBytes())));
    }
}
