package com.auction.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${app.security.jwt.secret-key}")
    private String secretKeyString;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. Skip token validation for public endpoints and WebSocket paths if needed
        if (path.contains("/api/v1/auth/") || path.contains("/ws-notifications")) {
            return chain.filter(exchange);
        }

        // 2. Extract Token with multiple strategies
        String token = extractToken(request);
        if (token == null) {
            return onError(exchange, "Missing or invalid Authorization header or token query parameter", HttpStatus.UNAUTHORIZED);
        }

        try {
            // 3. Decode and validate token signature
            SecretKey key = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // 4. Propagate Identity down to microservices via secure mutated headers
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", claims.getSubject())
                    .header("X-User-Roles", claims.get("roles", String.class))
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            log.error("JWT validation breakdown: {}", e.getMessage());
            return onError(exchange, "Token signature identification failed", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Tries extracting the token cleanly from standard headers, lowercase headers, and query strings.
     */
    private String extractToken(ServerHttpRequest request) {
        // Strategy A: Standard header check
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // Strategy B: Case-insensitive fallback for tools like Postman WebSockets
        if (authHeader == null) {
            authHeader = request.getHeaders().getFirst("authorization");
        }

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // Strategy C: Check query string parameters (?token=ey...)
        String queryToken = request.getQueryParams().getFirst("token");
        if (queryToken != null && !queryToken.trim().isEmpty()) {
            return queryToken;
        }

        return null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        log.warn("Security Reject Applied: {}", err);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}