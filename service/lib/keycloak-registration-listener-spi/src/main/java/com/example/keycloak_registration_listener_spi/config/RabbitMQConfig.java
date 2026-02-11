package com.example.keycloak_registration_listener_spi.config;

import lombok.*;
import org.keycloak.Config;

@Value
@Builder
public class RabbitMQConfig {
    private String host;
    private int port;
    private String username;
    private String password;
    private String virtualHost;

    public static RabbitMQConfig from(Config.Scope config) {
        return RabbitMQConfig.builder()
                .host(config.get("rabbitmqhost", "localhost"))
                .port(Integer.parseInt(config.get("rabbitmqport", "5672")))
                .username(config.get("rabbitmqusername", "guest"))
                .password(config.get("rabbitmqpassword", "guest"))
                .virtualHost(config.get("rabbitmqvirtualhost", "/"))
                .build();
    }
}
