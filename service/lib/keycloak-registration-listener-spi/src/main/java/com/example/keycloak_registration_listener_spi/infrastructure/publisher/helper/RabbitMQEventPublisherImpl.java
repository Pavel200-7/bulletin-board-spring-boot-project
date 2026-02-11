package com.example.keycloak_registration_listener_spi.infrastructure.publisher.helper;

import com.example.keycloak_registration_listener_spi.config.RabbitMQConfig;
import com.example.rabbitMQ_events_contracts.contracts.event.base.BaseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import com.rabbitmq.client.*;

@Slf4j
public class RabbitMQEventPublisherImpl implements RabbitMQEventPublisher {

    private final RabbitMQConfig config;
    private Connection connection;
    private Channel channel;

    private final ObjectMapper objectMapper;

    public RabbitMQEventPublisherImpl(RabbitMQConfig config) {
        this.config = config;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        connect();
    }

    private void connect() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(config.getHost());
            factory.setPort(config.getPort());
            factory.setUsername(config.getUsername());
            factory.setPassword(config.getPassword());
            factory.setVirtualHost(config.getVirtualHost());

            this.connection = factory.newConnection();
            this.channel = connection.createChannel();

            log.info("Подключено к RabbitMQ: {}:{}", config.getHost(), config.getPort());

        } catch (Exception e) {
            log.info("host and port: {}:{}", config.getHost(), config.getPort());
            log.error("Ошибка подключения к RabbitMQ: {}", e.getMessage());
        }
    }

    @Override
    public void convertAndSend(String exchange, String routingKey, BaseEvent event) {
        if (channel == null || !channel.isOpen()) {
            log.warn("RabbitMQ не подключен, пропуск события");
            return;
        }

        try {
            channel.exchangeDeclare(exchange, "topic", true);
            String json = objectMapper.writeValueAsString(event);
            channel.basicPublish(exchange, routingKey, null, json.getBytes());
            log.info("Отправлено: {} -> {}", routingKey, event.getEventId());
        } catch (Exception e) {
            log.error("Ошибка отправки в RabbitMQ: {}", e.getMessage(), e);
        }
    }



    public void close() {
        try {
            if (channel != null && channel.isOpen()) channel.close();
            if (connection != null && connection.isOpen()) connection.close();
        } catch (Exception e) {
            log.error("Ошибка закрытия соединения RabbitMQ: {}", e.getMessage());
        }
    }
}
