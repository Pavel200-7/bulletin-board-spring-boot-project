package com.example.chat.infrastructure.security;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SecurityService {
    Jwt getCurrentJwt();
    String getCurrentUserId();
    UUID getCurrentUserIdAsUUID();
    String getCurrentUsername();
    String getCurrentEmail();
    List<String> getCurrentRoles();
    boolean hasRole(String role);
    boolean isAdmin();
    boolean isCurrentUser(UUID userId);
    Map<String, Object> getAllClaims();
    <T> T getClaim(String claim, Class<T> type);
}
