package com.example.keycloak_registration_listener_spi.keycloak;

import com.example.keycloak_registration_listener_spi.config.RabbitMQConfig;
import com.example.keycloak_registration_listener_spi.infrastructure.publisher.EventPublisher;
import com.example.keycloak_registration_listener_spi.infrastructure.publisher.EventPublisherImpl;
import com.example.keycloak_registration_listener_spi.infrastructure.publisher.helper.RabbitMQEventPublisher;
import com.example.keycloak_registration_listener_spi.infrastructure.publisher.helper.RabbitMQEventPublisherImpl;
import com.example.keycloak_registration_listener_spi.keycloak.helper.impl.AdminEventProcessorImpl;
import com.example.keycloak_registration_listener_spi.keycloak.helper.impl.UserEventProcessorImpl;
import com.example.keycloak_registration_listener_spi.keycloak.helper.inter.AdminEventProcessor;
import com.example.keycloak_registration_listener_spi.keycloak.helper.inter.UserEventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@Slf4j
@RequiredArgsConstructor
public class MyEventListenerProviderFactory implements EventListenerProviderFactory {

    private EventPublisher publisher;

    @Override
    public void init(Config.Scope config) {
        log.info("Инициализация MyEventListenerProviderFactory");
        RabbitMQConfig rabbitMQConfig = RabbitMQConfig.from(config);
        RabbitMQEventPublisher publisherBase = new RabbitMQEventPublisherImpl(rabbitMQConfig);
        publisher = new EventPublisherImpl(publisherBase);
        log.info("Настройка MQ завершена");
    }

    @Override
    public org.keycloak.events.EventListenerProvider create(KeycloakSession session) {
        log.info("Создание MyEventListenerProvider");
        UserEventProcessor userEventProcessor = new UserEventProcessorImpl(session, publisher);
        AdminEventProcessor adminEventProcessor = new AdminEventProcessorImpl(session, publisher);
        return new MyEventListenerProvider(userEventProcessor, adminEventProcessor);
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        log.info("Post-init MyEventListenerProviderFactory");
    }

    @Override
    public void close() {
        log.info("Закрытие MyEventListenerProviderFactory");
    }

    @Override
    public String getId() {
        return "my-event-listener";
    }
}
