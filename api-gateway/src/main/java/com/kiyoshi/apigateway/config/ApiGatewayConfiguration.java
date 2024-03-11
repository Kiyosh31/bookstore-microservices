package com.kiyoshi.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth_user_service_route", r -> r.path("/api/v1/user/**")
                        .uri("lb://USER-SERVICE"))
                .route("auth_book_service_route", r -> r.path("/api/v1/book/**")
                        .uri("lb://BOOK-SERVICE"))
                .route("auth_stock_service_route", r -> r.path("/api/v1/stock/**")
                        .uri("lb://STOCK-SERVICE"))
                .route("auth_order_service_route", r -> r.path("/api/v1/order/**")
                        .uri("lb://ORDER-SERVICE"))
                .route("auth_notification_service_route", r -> r.path("/api/v1/notification/**")
                        .uri("lb://NOTIFICATION-SERVICE"))
                .build();
    }
}
