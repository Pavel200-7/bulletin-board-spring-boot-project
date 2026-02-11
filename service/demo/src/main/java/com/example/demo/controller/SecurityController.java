package com.example.demo.controller;

import com.example.demo.infrastructure.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/security")
public class SecurityController {

    @Autowired
    private SecurityService securityService;

    @GetMapping("/claims/login")
    public String getClaims() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        DefaultOidcUser user = (DefaultOidcUser) auth.getPrincipal();
        OidcIdToken token = user.getIdToken();
        Map<String, Object> claims = token.getClaims();
        return "OIDC User Claims: " + claims.toString();
    }

    @GetMapping("/claims/resource")
    public String getClaimsResource() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) auth.getPrincipal();
        Map<String, Object> claims = jwt.getClaims();
        return "JWT Claims: " + claims.toString();
    }

    @GetMapping("/test")
    public String test() {
        return "тестовый.";
    }

    @PreAuthorize("@testEvaluator.hasPermission(authentication, #num)")
    @GetMapping("/test/permission_evaluator/{num}")
    public String testPermissionEvaluator(@PathVariable(name = "num") int num) {
        return "Прошел";
    }
}
