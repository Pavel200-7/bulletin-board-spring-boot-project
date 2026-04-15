package com.example.bulletin.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import({
        TestOAuth2Config.class,
        TestSecurityConfig.class
})
public class TestConfig {
    // Пустой класс, просто агрегатор конфигураций
}