package com.example.keycloak_registration_listener_spi.infrastructure.publisher;

import com.example.rabbitMQ_events_contracts.contracts.event.base.BaseEvent;

public interface EventPublisher {
    void publishEvent(BaseEvent event);
}