package com.apigateway.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Routes {

    private final String birthdayServicePath = "http://birthdayservice:8080";
    private final String imageServicePath = "http://imageservice:8080";
    private final String notificationServicePath = "http://notificationservice:8080";


    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("friend_route", r -> r.path("/api/v1/friend**", "/api/v1/friend/**")
                        .uri(birthdayServicePath))
                .route("image_route", r -> r.path("/api/v1/image**", "/api/v1/image/**")
                        .uri(imageServicePath))
                .route("subscriber_route", r -> r.path("/api/v1/subscriber**", "/api/v1/subscriber/**")
                        .uri(notificationServicePath))
                .route("subscription_route", r -> r.path("/api/v1/subscription**", "/api/v1/subscription/**")
                        .uri(notificationServicePath))
                .build();
    }
}
