package com.example.bulletin.infrastructure.publisher;

import com.example.rabbitMQ_events_contracts.contracts.ExchangeContract;
import com.example.rabbitMQ_events_contracts.contracts.event.base.BaseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQEventPublisherImpl implements RabbitMQEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void send(BaseEvent event) {
        rabbitTemplate.convertAndSend(
                ExchangeContract.BULLETIN_EXCHANGE,
                event.getEventType().getRoutingKey(),
                event
        );
        log.info("Было отправлено сообщение {}", event);
    }

}
