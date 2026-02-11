package com.example.keycloak_registration_listener_spi.infrastructure.publisher.helper;

import com.example.rabbitMQ_events_contracts.contracts.event.base.BaseEvent;

public interface RabbitMQEventPublisher {
    void convertAndSend(String exchange, String routingKey, BaseEvent event);
}
