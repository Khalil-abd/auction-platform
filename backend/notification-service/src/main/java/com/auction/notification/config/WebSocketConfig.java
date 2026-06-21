package com.auction.notification.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Destination prefix for clients to subscribe to live channels
        config.enableSimpleBroker("/topic");
        // Prefix for messages bound for server-side router methods
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 1. For your Angular frontend (retains SockJS support)
        registry.addEndpoint("/ws-notifications")
                .setAllowedOrigins("http://localhost:4200")
                .withSockJS();

        // 2. Dedicated path for raw testing tools like Postman (No SockJS)
        registry.addEndpoint("/ws-raw")
                .setAllowedOrigins("*");
    }
}