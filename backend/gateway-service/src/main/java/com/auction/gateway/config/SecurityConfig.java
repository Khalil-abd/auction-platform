package com.auction.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // Public entry endpoints (auth, registration, etc.)
                        .pathMatchers("/api/v1/auth/**").permitAll()
                        // Allow internal service discovery/health lines if needed
                        .pathMatchers("/actuator/health").permitAll()
                        // All other transactions must pass authentication checks
                        .anyExchange().authenticated()
                )
                .build();
    }
}