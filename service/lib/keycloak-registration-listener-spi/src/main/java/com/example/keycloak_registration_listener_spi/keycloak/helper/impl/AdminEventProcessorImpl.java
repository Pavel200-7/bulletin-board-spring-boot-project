package com.example.keycloak_registration_listener_spi.keycloak.helper.impl;

import com.example.keycloak_registration_listener_spi.infrastructure.publisher.EventPublisher;
import com.example.keycloak_registration_listener_spi.keycloak.helper.inter.AdminEventProcessor;
import com.example.rabbitMQ_events_contracts.contracts.event.UserBlockedEvent;
import com.example.rabbitMQ_events_contracts.contracts.event.UserRegisteredEvent;
import com.example.rabbitMQ_events_contracts.contracts.event.UserUnblockedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

@Slf4j
@RequiredArgsConstructor
public class AdminEventProcessorImpl implements AdminEventProcessor {

    private final KeycloakSession session;
    private final EventPublisher publisher;

    @Override
    public void processRegistrationEvent(AdminEvent event) {
        log.info("Администратор создал пользователя");

        String resourcePath = event.getResourcePath();
        if (!resourcePath.startsWith("users/")) {
            return;
        }

        String userId = extractUserIdFromPath(event.getResourcePath());
        if (userId == null) {
            return;
        }

        UserModel user = getUserById(event.getRealmId(), userId);
        if (user == null) {
            return;
        }

        UserRegisteredEvent eventMQ = createRegistrationEventFromUser(user);
        log.info("Получен пользователь (создан админом): {}", eventMQ);
        publisher.publishEvent(eventMQ);
    }

    private UserRegisteredEvent createRegistrationEventFromUser(UserModel user) {
        return UserRegisteredEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    @Override
    public void processUserBlockEvent(AdminEvent event) {
        log.info("Администратор блокирует пользователя");

        String userId = extractUserIdFromPath(event.getResourcePath());
        if (userId == null) {
            return;
        }

        UserModel user = getUserById(event.getRealmId(), userId);
        if (user == null) {
            log.warn("Пользователь с ID {} не найден при попытке блокировки", userId);
            return;
        }

        UserBlockedEvent blockEvent = UserBlockedEvent.builder()
                .userId(user.getId())
                .build();

        log.info("Пользователь заблокирован: {}", blockEvent);
        publisher.publishEvent(blockEvent);
    }

    @Override
    public void processUserUnblockEvent(AdminEvent event) {
        log.info("Администратор разблокирует пользователя");

        String userId = extractUserIdFromPath(event.getResourcePath());
        if (userId == null) {
            return;
        }

        UserModel user = getUserById(event.getRealmId(), userId);
        if (user == null) {
            log.warn("Пользователь с ID {} не найден при попытке разблокировки", userId);
            return;
        }

        UserUnblockedEvent unblockEvent = UserUnblockedEvent.builder()
                .userId(user.getId())
                .build();

        log.info("Пользователь разблокирован: {}", unblockEvent);
        publisher.publishEvent(unblockEvent);
    }

    private String extractUserIdFromPath(String resourcePath) {
        if (resourcePath == null || !resourcePath.startsWith("users/")) {
            return null;
        }
        return resourcePath.substring(6);
    }

    private UserModel getUserById(String realmId, String userId) {
        try {
            RealmModel realm = session.realms().getRealm(realmId);
            return session.users().getUserById(realm, userId);
        } catch (Exception e) {
            log.error("Ошибка при получении пользователя по ID: {}", userId, e);
            return null;
        }
    }

}
