package com.example.bulletin.host.controller;


import com.example.bulletin.infrastructure.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
public class TestController {

    private final SecurityService securityService;

    @GetMapping
    public String isAdmin() {
        return securityService.isAdmin() ? "admin" : "no";
    }
}
