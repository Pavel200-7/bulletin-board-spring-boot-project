package com.example.notification.host.consumer.user;

import com.example.rabbitMQ_events_contracts.contracts.QueueContract;
import com.example.rabbitMQ_events_contracts.contracts.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisteredEventListener {


    @RabbitListener(queues = "FUTURE")
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        log.info("Получено событие UserRegisteredEvent: {}", event);

        log.info("Аккаунт торговой площадки для пользователя с id {} создан", event.getUserId());
    }

}