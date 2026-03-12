package com.example.chat.conf.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.keycloak")
public class SecurityConfigProperties {
    private String clientId;
    private String clientSecret;
    private String outUri;
    private String dockerUri;
}
