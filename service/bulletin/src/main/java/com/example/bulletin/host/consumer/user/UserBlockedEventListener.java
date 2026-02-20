package com.example.bulletin.host.consumer.user;

import com.example.bulletin.application.service.user.UserService;
import com.example.bulletin.application.service.user.data.request.BlockUserRequest;
import com.example.bulletin.application.service.user.data.request.CreateUserRequest;
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

    private final UserService service;

    @RabbitListener(queues = QueueContract.BULLETIN_USER_BLOCKED_QUEUE)
    public void handleUserBlockedEvent(UserBlockedEvent event) {
        log.info("Получено событие UserBlockedEvent: {}", event);
        BlockUserRequest request = BlockUserRequest.builder()
                .id(UUID.fromString(event.getUserId()))
                .build();
        service.blockUser(request);
        log.info("Пользователь с id {} заблокирован: {}", event.getUserId());
    }

}
