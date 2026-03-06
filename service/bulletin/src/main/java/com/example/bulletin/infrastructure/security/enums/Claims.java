package com.example.bulletin.infrastructure.security.enums;

import lombok.Getter;

public enum Claims {
    CURRENT_USER_ID("sub"),
    CURRENT_USER_NAME("preferred_username"),
    CURRENT_USER_EMAIL("email"),
    CURRENT_USER_ROLES("spring_sec_roles");

    public final String name;

    Claims(String name) {
        this.name = name;
    }

}
