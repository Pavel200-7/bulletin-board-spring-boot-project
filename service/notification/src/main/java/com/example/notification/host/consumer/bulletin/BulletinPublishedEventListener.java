package com.example.notification.host.consumer.bulletin;

import com.example.notification.application.service.notification.NotificationService;
import com.example.notification.application.service.notification.data.request.SendBulletinPublishedNotificationRequest;
import com.example.notification.application.service.user.UserService;
import com.example.notification.application.service.user.data.request.CreateUserRequest;
import com.example.notification.application.service.user.data.response.CreateUserResponse;
import com.example.rabbitMQ_events_contracts.contracts.QueueContract;
import com.example.rabbitMQ_events_contracts.contracts.event.UserRegisteredEvent;
import com.example.rabbitMQ_events_contracts.contracts.event.bulletin.BulletinPublishedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BulletinPublishedEventListener {

    private final NotificationService service;

    @RabbitListener(queues = QueueContract.NOTIFICATION_BULLETIN_PUBLISHED_QUEUE)
    public void handleBulletinPublishedEvent(BulletinPublishedEvent event) {
        log.info("Получено событие BulletinPublishedEvent: {}", event);
        SendBulletinPublishedNotificationRequest request = SendBulletinPublishedNotificationRequest.builder()
                .publisherId(event.getPublisherId())
                .bulletinName(event.getBulletinName())
                .price(event.getPrice())
                .publisherName(event.getPublisherName())
                .build();

        service.sendBulletinPublishedNotification(request);
        log.info("Событие BulletinPublishedEvent обработано.");
    }

}