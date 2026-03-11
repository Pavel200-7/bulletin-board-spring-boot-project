package com.example.notification.host.consumer.user;

import com.example.rabbitMQ_events_contracts.contracts.QueueContract;
import com.example.rabbitMQ_events_contracts.contracts.event.UserBlockedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserBlockedEventListener {


    @RabbitListener(queues = "FUTURE")
    public void handleUserBlockedEvent(UserBlockedEvent event) {
        log.info("Получено событие UserBlockedEvent: {}", event);

        log.info("Пользователь с id {} заблокирован: {}", event.getUserId());
    }

}
