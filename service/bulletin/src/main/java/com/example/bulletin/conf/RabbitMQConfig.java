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
    public Queue bulletinQueue() {
        String queueName = QueueContract.BULLETIN_QUEUE;
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", queueName.concat(".dlx"))
                .build();
    }

    @Bean
    public Binding userCreatedBinding() {
        return BindingBuilder.bind(bulletinQueue())
                .to(userExchange())
                .with(EventType.USER_REGISTERED.getRoutingKey());
    }


    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

}
