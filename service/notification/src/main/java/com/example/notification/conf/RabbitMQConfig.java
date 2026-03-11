package com.example.notification.conf;

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
    public TopicExchange bulletinExchange() {
        return new TopicExchange(ExchangeContract.BULLETIN_EXCHANGE);
    }

    @Bean
    public Queue userRegisteredQueue() {
        return QueueBuilder.durable(QueueContract.NOTIFICATION_USER_REGISTERED_QUEUE)
                .withArgument("x-dead-letter-exchange", QueueContract.NOTIFICATION_USER_REGISTERED_QUEUE.concat(".dlx"))
                .build();
    }

    @Bean
    public Queue bulletinPublishedQueue() {
        return QueueBuilder.durable(QueueContract.NOTIFICATION_BULLETIN_PUBLISHED_QUEUE)
                .withArgument("x-dead-letter-exchange", QueueContract.NOTIFICATION_BULLETIN_PUBLISHED_QUEUE.concat(".dlx"))
                .build();
    }

    @Bean
    public Binding userRegisteredBinding() {
        return BindingBuilder.bind(userRegisteredQueue())
                .to(userExchange())
                .with(EventType.USER_REGISTERED.getRoutingKey());
    }

    @Bean
    public Binding bulletinPublishedBinding() {
        return BindingBuilder.bind(bulletinPublishedQueue())
                .to(bulletinExchange())
                .with(EventType.BULLETIN_PUBLISHED.getRoutingKey());
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

}
