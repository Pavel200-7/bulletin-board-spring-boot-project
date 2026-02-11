package com.example.keycloak_registration_listener_spi.infrastructure.publisher;

import com.example.rabbitMQ_events_contracts.contracts.event.UserRegisteredEvent;

public interface EventPublisher {
    public void publishUserRegistered(UserRegisteredEvent event);
}