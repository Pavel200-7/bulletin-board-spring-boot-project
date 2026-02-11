package com.example.rabbitMQ_events_contracts.contracts;

import lombok.Getter;

@Getter
public enum EventType {
    USER_REGISTERED("user.registered");
//    USER_UPDATED("user.updated"),  // на будущее
//    USER_DELETED("user.deleted"),
//    LOGIN_SUCCESS("user.login.success"),
//    LOGIN_FAILED("user.login.failed"),
//    LOGOUT("user.logout"),
//    PASSWORD_CHANGED("user.password.changed");

    public final String routingKey;

    EventType(String routingKey) {
        this.routingKey = routingKey;
    }
}