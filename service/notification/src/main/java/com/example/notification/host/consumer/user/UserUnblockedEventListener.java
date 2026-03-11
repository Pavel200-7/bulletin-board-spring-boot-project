package com.example.notification.host.consumer.user;

import com.example.rabbitMQ_events_contracts.contracts.event.UserUnblockedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserUnblockedEventListener {


    @RabbitListener(queues = "FUTURE")
    public void handleUserUnblockedEvent(UserUnblockedEvent event) {
        log.info("Получено событие UserUnblockedEvent: {}", event);

        log.info("Пользователь с id {} разблокирован: {}", event.getUserId());
    }
}
