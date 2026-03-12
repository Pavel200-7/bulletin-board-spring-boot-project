package com.example.notification.application.service.notification;

import com.example.notification.application.service.notification.data.request.SendBulletinPublishedNotificationRequest;

public interface NotificationService {
    void sendBulletinPublishedNotification(SendBulletinPublishedNotificationRequest request);
}
