package com.example.bulletin.host.consumer.user;

import com.example.bulletin.application.service.user.UserService;
import com.example.bulletin.application.service.user.data.request.UnblockUserRequest;
import com.example.rabbitMQ_events_contracts.contracts.QueueContract;
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

    private final UserService service;

    @RabbitListener(queues = QueueContract.BULLETIN_USER_UNBLOCKED_QUEUE)
    public void handleUserUnblockedEvent(UserUnblockedEvent event) {
        log.info("Получено событие UserUnblockedEvent: {}", event);
        UnblockUserRequest request = UnblockUserRequest.builder()
                .id(UUID.fromString(event.getUserId()))
                .build();
        service.unblockUser(request);
        log.info("Пользователь с id {} разблокирован: {}", event.getUserId());
    }
}
