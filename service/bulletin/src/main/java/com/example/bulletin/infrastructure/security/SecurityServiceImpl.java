package com.example.bulletin.infrastructure.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class SecurityServiceImpl implements SecurityService {

    public Jwt getCurrentJwt() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken();
        }
        throw new IllegalStateException("No JWT token found in security context");
    }

    public String getCurrentUserId() {
        return getCurrentJwt().getClaimAsString("sub");
    }

    public UUID getCurrentUserIdAsUUID() {
        return UUID.fromString(getCurrentUserId());
    }

    public String getCurrentUsername() {
        return getCurrentJwt().getClaimAsString("preferred_username");
    }

    public String getCurrentEmail() {
        return getCurrentJwt().getClaimAsString("email");
    }

    public List<String> getCurrentRoles() {
        return getCurrentJwt().getClaimAsStringList("spring_sec_roles");
    }

    public boolean hasRole(String role) {
        List<String> roles = getCurrentRoles();
        return roles != null && roles.contains(role);
    }

    public boolean isCurrentUser(UUID userId) {
        return getCurrentUserIdAsUUID().equals(userId);
    }

    public Map<String, Object> getAllClaims() {
        return getCurrentJwt().getClaims();
    }

    public <T> T getClaim(String claim, Class<T> type) {
        return getCurrentJwt().getClaim(claim);
    }

}
