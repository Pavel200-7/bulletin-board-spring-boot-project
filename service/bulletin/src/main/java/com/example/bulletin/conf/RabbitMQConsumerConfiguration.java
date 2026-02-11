package com.example.bulletin.conf;

import com.example.rabbitMQ_events_contracts.contracts.*;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableRabbit
public class RabbitMQConsumerConfiguration {

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

//    @Bean
//    public ObjectMapper objectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        return objectMapper;
//    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

//    @Bean
//    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
//            ConnectionFactory connectionFactory,
//            JacksonJsonMessageConverter messageConverter) {
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory);
//        factory.setMessageConverter(messageConverter);
//        return factory;
//    }

}
