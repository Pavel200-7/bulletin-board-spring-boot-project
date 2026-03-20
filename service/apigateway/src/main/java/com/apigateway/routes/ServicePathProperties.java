package com.apigateway.routes;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.service-path")
public class ServicePathProperties {
    private String bulletin;
    private String notification;
    private String chat;
    private String keycloak;
    private String auth;

    public String getFullPath(String path) {
        return "http://" + path;
    }
}
