package com.example.keycloak_registration_listener_spi.infrastructure.publisher;

import com.example.keycloak_registration_listener_spi.infrastructure.publisher.helper.RabbitMQEventPublisher;
import com.example.rabbitMQ_events_contracts.contracts.ExchangeContract;
import com.example.rabbitMQ_events_contracts.contracts.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventPublisherImpl implements EventPublisher {

    private final RabbitMQEventPublisher publisher;

    @Override
    public void publishUserRegistered(UserRegisteredEvent event) {
        publisher.convertAndSend(
                ExchangeContract.USER_EXCHANGE,
                event.getEventType().getRoutingKey(),
                event
        );
    }
}
