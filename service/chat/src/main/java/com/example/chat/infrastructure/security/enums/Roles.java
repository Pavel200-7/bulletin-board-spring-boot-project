package com.example.chat.infrastructure.security.enums;

public enum Roles {
    USER("user"),
    ADMIN("admin");

    public final String name;

    private Roles(String name) {
        this.name = name;
    }
}
