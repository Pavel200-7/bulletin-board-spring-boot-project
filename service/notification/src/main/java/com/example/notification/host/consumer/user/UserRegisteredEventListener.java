package com.example.notification.host.consumer.user;

import com.example.notification.application.service.user.UserService;
import com.example.notification.application.service.user.data.request.CreateUserRequest;
import com.example.notification.application.service.user.data.response.CreateUserResponse;
import com.example.rabbitMQ_events_contracts.contracts.QueueContract;
import com.example.rabbitMQ_events_contracts.contracts.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisteredEventListener {

    private final UserService service;


    @RabbitListener(queues = QueueContract.NOTIFICATION_USER_REGISTERED_QUEUE)
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        log.info("Получено событие UserRegisteredEvent: {}", event);
        CreateUserRequest request = CreateUserRequest.builder()
                .id(UUID.fromString(event.getUserId()))
                .email(event.getEmail())
                .build();
        CreateUserResponse response = service.createUser(request);
        log.info("Пользователь с id {} создан", event.getUserId());
    }

}