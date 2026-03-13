package com.example.chat.infrastructure.security;

import com.example.chat.infrastructure.security.enums.Claims;
import com.example.chat.infrastructure.security.enums.Roles;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class SecurityServiceImpl implements SecurityService {

    @Override
    public Jwt getCurrentJwt() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken();
        }
        throw new IllegalStateException("No JWT token found in security context");
    }

    @Override
    public String getCurrentUserId() {
        return getCurrentJwt().getClaimAsString(
                Claims.CURRENT_USER_ID.name);
    }

    @Override
    public UUID getCurrentUserIdAsUUID() {
        return UUID.fromString(getCurrentUserId());
    }

    @Override
    public String getCurrentUsername() {
        return getCurrentJwt().getClaimAsString(
                Claims.CURRENT_USER_NAME.name);
    }

    @Override
    public String getCurrentEmail() {
        return getCurrentJwt().getClaimAsString(
                Claims.CURRENT_USER_EMAIL.name);
    }

    @Override
    public List<String> getCurrentRoles() {
        return getCurrentJwt().getClaimAsStringList(
                Claims.CURRENT_USER_ROLES.name);
    }

    @Override
    public boolean hasRole(String role) {
        List<String> roles = getCurrentRoles();
        return roles != null && roles.contains(role);
    }

    @Override
    public boolean isAdmin() {
        return hasRole(Roles.ADMIN.name);
    }

    @Override
    public boolean isCurrentUser(UUID userId) {
        return getCurrentUserIdAsUUID().equals(userId);
    }

    @Override
    public Map<String, Object> getAllClaims() {
        return getCurrentJwt().getClaims();
    }

    @Override
    public <T> T getClaim(String claim, Class<T> type) {
        return getCurrentJwt().getClaim(claim);
    }

}
