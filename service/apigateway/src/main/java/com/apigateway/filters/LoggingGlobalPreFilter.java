package com.apigateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingGlobalPreFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingGlobalPreFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String fullUrl = request.getURI().toString();
        String method = request.getMethod().name();
        String host = request.getRemoteAddress() != null
                ? request.getRemoteAddress().getHostString()
                : "unknown";
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        logger.info(">>> GATEWAY REQUEST: {} {} from {}", method, fullUrl, host);
        if (authHeader != null) {
            logger.info("Authorization header present: {}", authHeader.substring(0, Math.min(30, authHeader.length())) + "...");
        } else {
            logger.warn("No Authorization header in request!");
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }

}