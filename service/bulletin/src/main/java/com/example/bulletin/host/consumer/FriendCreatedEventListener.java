package com.example.bulletin.host.consumer;

import com.example.rabbitMQ_events_contracts.contracts.QueueContract;
import com.example.rabbitMQ_events_contracts.contracts.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendCreatedEventListener {

    @RabbitListener(queues = QueueContract.BULLETIN_QUEUE)
    public void handleFriendCreatedEvent(UserRegisteredEvent event) {
        log.info("Получено событие UserRegisteredEvent: {}", event);
    }

}