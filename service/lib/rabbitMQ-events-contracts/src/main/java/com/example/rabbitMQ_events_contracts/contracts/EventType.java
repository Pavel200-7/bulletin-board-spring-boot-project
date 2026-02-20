package com.example.rabbitMQ_events_contracts.contracts;

import lombok.Getter;

@Getter
public enum EventType {
    USER_REGISTERED("user.registered"),
    USER_BLOCKED("user.blocked"),
    USER_UNBLOCKED("user.unblocked");

    public final String routingKey;

    EventType(String routingKey) {
        this.routingKey = routingKey;
    }
}