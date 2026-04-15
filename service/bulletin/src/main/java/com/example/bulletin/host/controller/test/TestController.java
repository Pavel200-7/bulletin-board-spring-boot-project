package com.example.bulletin.host.controller.test;


import com.example.bulletin.host.controller.test.helper.DatabaseFillerService;
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
    private final DatabaseFillerService databaseFillerService;

    @GetMapping("/is-admin")
    public String isAdmin() {
        return securityService.isAdmin() ? "admin" : "no";
    }

    @GetMapping("/id")
    public String getId() {
        return securityService.getCurrentUserId();
    }

    @GetMapping("fill-db")
    public String fillDatabase() {
        databaseFillerService.fillDatabase();
        return "БД заполнена тестовыми данными.";
    }

    @GetMapping("/is-alive")
    public String isAlive() {
        return "Yes";
    }

}
