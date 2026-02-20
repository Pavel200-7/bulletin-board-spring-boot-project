package com.example.keycloak_registration_listener_spi.keycloak.helper.impl;

import com.example.keycloak_registration_listener_spi.infrastructure.publisher.EventPublisher;
import com.example.keycloak_registration_listener_spi.keycloak.helper.inter.UserEventProcessor;
import com.example.rabbitMQ_events_contracts.contracts.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.events.Event;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

@Slf4j
@RequiredArgsConstructor
public class UserEventProcessorImpl implements UserEventProcessor {

    private final KeycloakSession session;
    private final EventPublisher publisher;

    @Override
    public void processRegistrationEvent(Event event) {
        log.info("Отрабатывает кастомный обработчик регистрации.");

        RealmModel realm = session.realms().getRealm(event.getRealmId());
        UserModel user = session.users().getUserById(realm, event.getUserId());
        if (user == null) {
            return;
        }

        UserRegisteredEvent eventMQ = createEventFromUser(user);
        log.info("Получен пользователь: {}", eventMQ.toString());
        publisher.publishEvent(eventMQ);
    }

    private UserRegisteredEvent createEventFromUser(UserModel user) {
        return UserRegisteredEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

}
