//package com.example.bulletin.conf.security;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.jwt.JwtValidators;
//import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
//
//@Configuration
//@RequiredArgsConstructor
//public class JwtDecoderConfig {
//    private final SecurityConfigProperties properties;
//
//    @Bean
//    @Profile("!test")
//    public JwtDecoder jwtDecoder() {
//        // Для запроса ключей
//        NimbusJwtDecoder decoder = NimbusJwtDecoder
//                .withJwkSetUri(properties.getDockerUri().concat("/protocol/openid-connect/certs"))
//                .build();
//        // для валидации подписи jwt
//        decoder.setJwtValidator(
//                JwtValidators.createDefaultWithIssuer(
//                        properties.getOutUri().concat("/protocol/openid-connect/auth")
//                                .replace("/protocol/openid-connect/auth", "")
//                )
//        );
//        return decoder;
//    }
//
//}
