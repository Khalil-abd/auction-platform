package com.auction.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class GatewayRoutingConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        // Allow your Angular frontend origin
        corsConfig.addAllowedOrigin("http://localhost:4200");
        corsConfig.addAllowedMethod("GET");
        corsConfig.addAllowedMethod("POST");
        corsConfig.addAllowedMethod("PUT");
        corsConfig.addAllowedMethod("DELETE");
        corsConfig.addAllowedMethod("OPTIONS");
        corsConfig.addAllowedHeader("*"); // Allow all headers (Authorization, Content-Type, etc.)
        corsConfig.setAllowCredentials(true); // Allow credentials like cookies or authorization headers
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig); // Apply this configuration globally to all paths passing through the gateway

        return new CorsWebFilter(source);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 1. Auth Service Route
                .route("auth-service-route", r -> r
                        .path("/api/v1/auth/**")
                        .uri("http://auth-service:8084"))
                // 2. Catalog Service Route
                .route("catalog-service-route", r -> r
                        .path("/api/v1/auctions/**")
                        .uri("http://catalog-service:8081"))
                // 3. Bidding Service Route
                .route("bidding-service-route", r -> r
                        .path("/api/v1/bids/**")
                        .uri("http://bidding-service:8082"))
                // 4. Real-Time Notification WebSocket Route
                .route("notification-service-websocket-route", r -> r
                        .path("/ws-raw/**")
                        .uri("ws://notification-service:8083"))
                // 5. Production Angular Frontend WebSocket Route (SockJS support)
                .route("notification-service-sockjs-route", r -> r
                        .path("/ws-notifications/**")
                        .uri("ws://notification-service:8083"))
                .build();
    }

}
