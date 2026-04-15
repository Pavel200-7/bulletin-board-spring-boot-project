package com.example.bulletin.conf.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.stream.Stream;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Profile("!test")
public class SecurityConfig {

    private final SecurityConfigProperties properties;

    // При развертывании в сети docker возникает проблема с подписями:
    // keycloak считает себя localhost:8080, а программа может обратится к ниему (за клемами проверки токенов и тд)
    // только по внутренему адресу сети docker keycloak:8080.
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(
                ClientRegistration.withRegistrationId("keycloak")
                        .clientId(properties.getClientId())
                        .clientSecret(properties.getClientSecret())
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .redirectUri("http://localhost:8085/login/oauth2/code/keycloak")
                        .scope("openid", "profile", "email")
                        .authorizationUri(properties.getOutUri().concat("/protocol/openid-connect/auth"))
                        .tokenUri(properties.getDockerUri().concat("/protocol/openid-connect/token"))
                        .jwkSetUri(properties.getDockerUri().concat("/protocol/openid-connect/certs"))
                        .userInfoUri(properties.getDockerUri().concat("/protocol/openid-connect/userinfo"))
                        .userNameAttributeName("preferred_username")
                        .clientName("Keycloak")
                        .build()
        );
    }

    @Bean
    @Profile("!test")
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests((authorize) -> authorize
                        // ========== ПУБЛИЧНЫЕ ЭНДПОИНТЫ (READ ONLY) ==========
                        // Работоспособность
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/actuator/info").permitAll()

                        // BulletinController
                        .requestMatchers("/health").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/bulletin/page").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/bulletin/**").permitAll()

                        // CategoryController
                        .requestMatchers(HttpMethod.GET, "/api/v1/category/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/category/root").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/category/family/**").permitAll()

                        // CharacteristicController
                        .requestMatchers(HttpMethod.GET, "/api/v1/characteristic/**").permitAll()

                        // CharacteristicValueController
                        .requestMatchers(HttpMethod.GET, "/api/v1/characteristic-value/**").permitAll()

                        // CategoryCharacteristicController
                        .requestMatchers(HttpMethod.GET, "/api/v1/category/*/characteristic").permitAll()

                        // CharacteristicCharacteristicValueController
                        .requestMatchers(HttpMethod.GET, "/api/v1/characteristic/*/characteristic-value").permitAll()

                        // TradeAccountController - GET запросы публичные
                        .requestMatchers(HttpMethod.GET, "/api/v1/trade-account/**").permitAll()

                        // ========== АУТЕНТИФИЦИРОВАННЫЕ ПОЛЬЗОВАТЕЛИ (любая роль) ==========
                        // BulletinController - операции с объявлениями
                        .requestMatchers(HttpMethod.POST, "/api/v1/bulletin").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/bulletin").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/bulletin/add-image").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/bulletin/remove-image").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/bulletin/main-image").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/bulletin/close").authenticated()

                        // TradeAccountController - все PUT запросы требуют аутентификации
                        .requestMatchers(HttpMethod.GET, "/api/v1/trade-account/my").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/trade-account/name").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/trade-account/phone").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/trade-account/contacts").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/trade-account/description").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/trade-account/approximate-location").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/trade-account/exact-location").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/trade-account/image").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/trade-account/approve").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/bulletin/approve").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/bulletin/publish").authenticated()

                        // ========== АДМИНИСТРАТОРЫ (только ADMIN) ==========
                        // Управление справочниками
                        .requestMatchers(HttpMethod.POST, "/api/v1/category/root").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/category/child").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/category/leafy-child").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/category/name").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/category/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/category/*/characteristic").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/characteristic/name").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/category/*/characteristic/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/characteristic/*/characteristic-value").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/characteristic-value/name").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/characteristic/*/characteristic-value/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );;
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtAuthenticationConverter.setPrincipalClaimName("preferred_username");
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            var authorities = jwtGrantedAuthoritiesConverter.convert(jwt);
            var roles =jwt.getClaimAsStringList("spring_sec_roles");

            // Не работает
//            return Stream.concat(authorities.stream(),
//                            roles.stream()
//                                    .filter(role -> role.startsWith("ROLE_"))
//                                    .map(SimpleGrantedAuthority::new)
//                                    .map(GrantedAuthority.class::cast))
//                    .toList();

            return Stream.concat(
                    authorities.stream(),
                    roles.stream()
                            .filter(role -> role != null && !role.isEmpty())
                            .map(role -> "ROLE_" + role.toUpperCase())
                            .map(SimpleGrantedAuthority::new)
                            .map(GrantedAuthority.class::cast)
            ).toList();
        });
        return jwtAuthenticationConverter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        log.info("Creating JwtDecoder...");
        log.info("  - JWK Set URI: {}", properties.getDockerUri().concat("/protocol/openid-connect/certs"));
        log.info("  - Expected issuer: {}", properties.getOutUri());

        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withJwkSetUri(properties.getDockerUri().concat("/protocol/openid-connect/certs"))
                .build();

        decoder.setJwtValidator(
                JwtValidators.createDefaultWithIssuer(properties.getOutUri())
        );

        return decoder;
    }

}