package com.example.notification.application.service.notification;

import com.example.notification.application.service.notification.data.request.SendBulletinPublishedNotificationRequest;
import jakarta.mail.MessagingException;

public interface NotificationService {
    void sendBulletinPublishedNotification(SendBulletinPublishedNotificationRequest request);
}
