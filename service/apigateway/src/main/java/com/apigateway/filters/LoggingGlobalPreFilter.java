package com.apigateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class LoggingGlobalPreFilter implements GlobalFilter {

    final Logger logger =
            LoggerFactory.getLogger(LoggingGlobalPreFilter.class);

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {
        logger.info("Global Pre Filter executed");

        ServerHttpRequest request = exchange.getRequest();
        String fullUrl = request.getURI().toString();
        String method = request.getMethod().name();
        String host = request.getRemoteAddress() != null
                ? request.getRemoteAddress().getHostString()
                : "unknown";

        logger.info(">>> GATEWAY REQUEST: {} {} from {}", method, fullUrl, host);
        return chain.filter(exchange);
    }
}