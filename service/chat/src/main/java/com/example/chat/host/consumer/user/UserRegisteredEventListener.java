package com.example.chat.host.consumer.user;

import com.example.chat.application.service.profile.ProfileService;
import com.example.chat.application.service.profile.data.request.CreateProfileRequest;
import com.example.chat.application.service.profile.data.response.CreateProfileResponse;
import com.example.chat.application.service.user.UserService;
import com.example.chat.application.service.user.data.request.CreateUserRequest;
import com.example.chat.application.service.user.data.response.CreateUserResponse;
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
    private final ProfileService profileService;

    @RabbitListener(queues = QueueContract.CHAT_USER_REGISTERED_QUEUE)
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        log.info("Получено событие UserRegisteredEvent: {}", event);
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .id(UUID.fromString(event.getUserId()))
                .email(event.getEmail())
                .build();
        CreateUserResponse response = userService.createUser(createUserRequest);
        log.info("User с id {} создан", event.getUserId());

        CreateProfileRequest createProfileRequest = CreateProfileRequest.builder()
                .ownerId(UUID.fromString(event.getUserId()))
                .ownerName(event.getFirstName())
                .build();
        CreateProfileResponse createProfileResponse = profileService.createProfile(createProfileRequest);
        log.info("Profile с id {} создан", createProfileResponse.getProfileResponse().getId());
    }

}