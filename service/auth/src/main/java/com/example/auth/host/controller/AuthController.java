package com.example.auth.host.controller;

import com.example.auth.host.controller.data.LoginResponse;
import com.example.auth.host.controller.data.LogoutRequest;
import com.example.auth.host.controller.data.RefreshTokenRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final WebClient webClient;

    @Value("${keycloak.auth-uri}")
    private String authUri;

    @Value("${keycloak.token-uri}")
    private String tokenUri;

    @Value("${keycloak.logout-uri}")
    private String logoutUri;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${backend.callback-uri}")
    private String backendCallbackUri;

    @Value("${frontend.redirect-uri}")
    private String frontendRedirectUri;

    @GetMapping("/authorize")
    public ResponseEntity<Void> authorize() {
        String redirectUri = UriComponentsBuilder.fromUriString(authUri)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", backendCallbackUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "openid profile email")
                .build()
                .encode()
                .toUriString();

        log.info("Redirecting to Keycloak login: {}", redirectUri);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirectUri))
                .build();
    }

    @GetMapping("/callback")
    public Mono<ResponseEntity<Void>> callback(@RequestParam("code") String code) {
        log.info("Received authorization code: {}", code);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);
        body.add("redirect_uri", backendCallbackUri);
        body.add("grant_type", "authorization_code");

        return webClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(body))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    String accessToken = (String) response.get("access_token");
                    String refreshToken = (String) response.get("refresh_token");

                    String redirectWithTokens = frontendRedirectUri +
                            "#access_token=" + accessToken +
                            "&refresh_token=" + refreshToken;

                    log.info("Successfully authenticated, redirecting to frontend");
                    return ResponseEntity.status(HttpStatus.FOUND)
                            .location(URI.create(redirectWithTokens))
                            .<Void>build();
                })
                .onErrorResume(e -> {
                    log.error("Failed to exchange code for tokens: {}", e.getMessage());
                    return Mono.just(
                            ResponseEntity.status(HttpStatus.FOUND)
                                    .location(URI.create(frontendRedirectUri + "?error=auth_failed"))
                                    .<Void>build()
                    );
                });
    }


    @PostMapping("/refresh")
    public Mono<ResponseEntity<LoginResponse>> refresh(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        log.info("Refreshing token");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);
        body.add("grant_type", "refresh_token");

        return webClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(body))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    LoginResponse loginResponse = LoginResponse.builder()
                            .accessToken((String) response.get("access_token"))
                            .refreshToken((String) response.get("refresh_token"))
                            .expiresIn((Integer) response.get("expires_in"))
                            .tokenType((String) response.get("token_type"))
                            .build();

                    log.info("Token refreshed successfully");
                    return ResponseEntity.ok(loginResponse);
                })
                .onErrorResume(e -> {
                    log.error("Failed to refresh token: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
                });
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(@RequestBody LogoutRequest request) {
        String refreshToken = request.getRefreshToken();
        log.info("Logging out");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);

        return webClient.post()
                .uri(logoutUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(body))
                .retrieve()
                .bodyToMono(Void.class)
                .map(response -> {
                    log.info("Successfully logged out");
                    return ResponseEntity.ok().<Void>build();
                })
                .onErrorResume(e -> {
                    log.error("Logout error: {}", e.getMessage());
                    return Mono.just(ResponseEntity.ok().<Void>build());
                });
    }
}