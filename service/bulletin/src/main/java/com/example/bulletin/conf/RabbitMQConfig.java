package com.example.bulletin.conf;

import com.example.rabbitMQ_events_contracts.contracts.*;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableRabbit
public class RabbitMQConfig {

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(ExchangeContract.USER_EXCHANGE);
    }

    @Bean
    public Queue userRegisteredQueue() {
        return QueueBuilder.durable(QueueContract.BULLETIN_USER_REGISTERED_QUEUE)
                .withArgument("x-dead-letter-exchange", QueueContract.BULLETIN_USER_REGISTERED_QUEUE.concat(".dlx"))
                .build();
    }

    // Очередь для блокировок
    @Bean
    public Queue userBlockedQueue() {
        return QueueBuilder.durable(QueueContract.BULLETIN_USER_BLOCKED_QUEUE)
                .withArgument("x-dead-letter-exchange", QueueContract.BULLETIN_USER_BLOCKED_QUEUE.concat(".dlx"))
                .build();
    }

    // Очередь для разблокировок
    @Bean
    public Queue userUnblockedQueue() {
        return QueueBuilder.durable(QueueContract.BULLETIN_USER_UNBLOCKED_QUEUE)
                .withArgument("x-dead-letter-exchange", QueueContract.BULLETIN_USER_UNBLOCKED_QUEUE.concat(".dlx"))
                .build();
    }

    @Bean
    public Binding userRegisteredBinding() {
        return BindingBuilder.bind(userRegisteredQueue())
                .to(userExchange())
                .with(EventType.USER_REGISTERED.getRoutingKey());
    }

    @Bean
    public Binding userBlockedBinding() {
        return BindingBuilder.bind(userBlockedQueue())
                .to(userExchange())
                .with(EventType.USER_BLOCKED.getRoutingKey());
    }

    @Bean
    public Binding userUnblockedBinding() {
        return BindingBuilder.bind(userUnblockedQueue())
                .to(userExchange())
                .with(EventType.USER_UNBLOCKED.getRoutingKey());
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

}
