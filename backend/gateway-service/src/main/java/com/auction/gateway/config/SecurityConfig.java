package com.auction.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Single source of truth for gateway access control.
 * The JwtAuthenticationFilter only enriches the security context —
 * all permit/deny decisions are made here.
 */
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
                        // Public endpoints — no token required
                        .pathMatchers("/api/v1/auth/**").permitAll()
                        .pathMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/auctions/**").permitAll()
                        .pathMatchers("/ws-notifications/**").permitAll()
                        .pathMatchers("/ws-raw/**").permitAll()
                        .pathMatchers("/actuator/health").permitAll()

                        // Protected endpoints — valid JWT required
                        .pathMatchers("/api/v1/auctions/**").authenticated()
                        .pathMatchers("/api/v1/bids/**").authenticated()

                        .anyExchange().authenticated()
                )
                .build();
    }
}