package com.example.bulletin.infrastructure.publisher;

import com.example.rabbitMQ_events_contracts.contracts.event.base.BaseEvent;

public interface RabbitMQEventPublisher {
    void send(BaseEvent event);
}
