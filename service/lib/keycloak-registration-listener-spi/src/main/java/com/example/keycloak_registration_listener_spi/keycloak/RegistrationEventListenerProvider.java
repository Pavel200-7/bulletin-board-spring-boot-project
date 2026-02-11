package com.example.keycloak_registration_listener_spi.keycloak;

import com.example.keycloak_registration_listener_spi.infrastructure.publisher.EventPublisher;
import com.example.rabbitMQ_events_contracts.contracts.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

@Slf4j
@RequiredArgsConstructor
public class RegistrationEventListenerProvider implements EventListenerProvider {

    private final KeycloakSession session;
    private final EventPublisher publisher;

    @Override
    public void onEvent(Event event) {
        if (event.getType() == EventType.REGISTER) {
            log.info("Отрабатывает кастомный обработчик регистрации.");

            RealmModel realm = session.realms().getRealm(event.getRealmId());
            UserModel user = session.users().getUserById(realm, event.getUserId());

            if (user != null) {
                UserRegisteredEvent eventMQ = createEventFromUser(user);
                log.info("Получен пользователь: {}", eventMQ.toString());
                publisher.publishUserRegistered(eventMQ);
            }
        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean b) {
        if (IsRegistrationEvent(event)) {
            log.info("Администратор создал пользователя");

            String resourcePath = event.getResourcePath();
            if (!resourcePath.startsWith("users/")) {
                return;
            }

            String userId = resourcePath.substring(6);
            RealmModel realm = session.realms().getRealm(event.getRealmId());
            UserModel user = session.users().getUserById(realm, userId);

            if (user == null) {
                return;
            }

            UserRegisteredEvent eventMQ = createEventFromUser(user);
            log.info("Получен пользователь (создан админом): {}", eventMQ);
            publisher.publishUserRegistered(eventMQ);
        }
    }

    @Override
    public void close() {
        log.info("EventListener закрыт");
    }

    private boolean IsRegistrationEvent(AdminEvent event) {
        return event.getOperationType().toString().equals("CREATE") &&
                event.getResourceType().toString().equals("USER");
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
