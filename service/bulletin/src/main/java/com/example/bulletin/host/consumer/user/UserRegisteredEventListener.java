package com.example.bulletin.host.consumer.user;

import com.example.bulletin.application.service.tradeaccount.TradeAccountService;
import com.example.bulletin.application.service.tradeaccount.data.request.CreateTradeAccountRequest;
import com.example.bulletin.application.service.user.UserService;
import com.example.bulletin.application.service.user.data.request.CreateUserRequest;
import com.example.bulletin.application.service.user.data.response.CreateUserResponse;
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

    private final UserService userService;
    private final TradeAccountService tradeAccountService;

    @RabbitListener(queues = QueueContract.BULLETIN_USER_REGISTERED_QUEUE)
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        log.info("Получено событие UserRegisteredEvent: {}", event);
        CreateUserRequest request = CreateUserRequest.builder()
                .id(UUID.fromString(event.getUserId()))
                .email(event.getEmail())
                .build();
        CreateUserResponse response = userService.createUser(request);
        log.info("Пользователь с id {} создан", event.getUserId());

        CreateTradeAccountRequest tradeAccountRequest = CreateTradeAccountRequest.builder()
                .ownerId(UUID.fromString(event.getUserId()))
                .build();
        tradeAccountService.createTradeAccount(tradeAccountRequest);
        log.info("Аккаунт торговой площадки для пользователя с id {} создан", event.getUserId());
    }

}