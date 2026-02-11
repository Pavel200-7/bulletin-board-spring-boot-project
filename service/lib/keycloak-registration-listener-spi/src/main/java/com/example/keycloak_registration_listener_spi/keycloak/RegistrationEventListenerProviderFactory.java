package com.example.keycloak_registration_listener_spi.keycloak;

import com.example.keycloak_registration_listener_spi.config.RabbitMQConfig;
import com.example.keycloak_registration_listener_spi.infrastructure.publisher.EventPublisher;
import com.example.keycloak_registration_listener_spi.infrastructure.publisher.EventPublisherImpl;
import com.example.keycloak_registration_listener_spi.infrastructure.publisher.helper.RabbitMQEventPublisher;
import com.example.keycloak_registration_listener_spi.infrastructure.publisher.helper.RabbitMQEventPublisherImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@Slf4j
@RequiredArgsConstructor
public class RegistrationEventListenerProviderFactory implements EventListenerProviderFactory {

    private EventPublisher publisher;

    @Override
    public void init(Config.Scope config) {
        log.info("Инициализация RegistrationEventListenerProviderFactory");
        RabbitMQConfig rabbitMQConfig = RabbitMQConfig.from(config);
        RabbitMQEventPublisher publisherBase = new RabbitMQEventPublisherImpl(rabbitMQConfig);
        publisher = new EventPublisherImpl(publisherBase);
        log.info("Настройка MQ завершена");
    }

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        log.info("Создание RegistrationEventListenerProvider");
        return new RegistrationEventListenerProvider(session, publisher);
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        log.info("Post-init RegistrationEventListenerProviderFactory");
    }

    @Override
    public void close() {
        log.info("Закрытие RegistrationEventListenerProviderFactory");
    }

    @Override
    public String getId() {
        return "registration-event-listener";
    }
}
