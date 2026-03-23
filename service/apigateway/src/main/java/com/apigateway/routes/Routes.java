package com.apigateway.routes;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class Routes {

    private final ServicePathProperties properties;

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
                        .uri(properties.getFullPath(properties.getBulletin()))
                )

                // ========== NOTIFICATION SERVICE ==========
                .route("notification_service", r -> r
                        .path("/api/v1/subscription/**")
                        .uri(properties.getFullPath(properties.getNotification()))
                )

                // ========== CHAT SERVICE ==========
                .route("chat_service", r -> r
                        .path(
                                "/api/v1/chat/**",
                                "/api/v1/contact/**",
                                "/api/v1/profile/**"
                        )
                        .uri(properties.getFullPath(properties.getChat()))
                )

                // ========== KEYCLOAK ==========
                .route("keycloak_service", r -> r
                        .path(
                                "/realms/**",
                                "/resources/**",
                                "/protocol/**",
                                "/admin/**"
                        )
                        .uri(properties.getFullPath(properties.getKeycloak()))
                )
                .route("auth_service", r -> r
                        .path("/api/v1/auth/**")
                        .filters(f -> f
                                .rewritePath("/api/v1/auth/(?<segment>.*)", "/api/v1/auth/${segment}")
                        )
                        .uri(properties.getFullPath(properties.getAuth()))
                )
                .build();
    }

}
