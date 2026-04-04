package com.apigateway.routes;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class Routes {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // ========== BULLETIN SERVICE ==========
                .route("bulletin_service", r -> r
                        .path(
                                "/api/v1/bulletin/**",
                                "/api/v1/category/**",
                                "/api/v1/characteristic/**",
                                "/api/v1/characteristic-value/**",
                                "/api/v1/trade-account/**"
                        )
                        .uri(ServiceUri.bulletin)
                )

                // ========== NOTIFICATION SERVICE ==========
                .route("notification_service", r -> r
                        .path("/api/v1/subscription/**")
                        .uri(ServiceUri.notification)
                )

                // ========== CHAT SERVICE ==========
                .route("chat_service", r -> r
                        .path(
                                "/api/v1/chat/**",
                                "/api/v1/contact/**",
                                "/api/v1/profile/**"
                        )
                        .uri(ServiceUri.chat)
                )

                // ========== CHAT SERVICE (WEBSOCKET) ==========
                .route("chat_service_ws", r -> r
                        .path("/api/v1/ws/**")
                        .filters(f -> f
                                .rewritePath("/api/v1/ws/(?<segment>.*)", "/ws/${segment}")
                        )
                        .uri(ServiceUri.chat)
                )
                .route("auth_service", r -> r
                        .path("/api/v1/auth/**")
                        .filters(f -> f
                                .rewritePath("/api/v1/auth/(?<segment>.*)", "/api/v1/auth/${segment}")
                        )
                        .uri(ServiceUri.auth)
                )
                .build();
    }

}
