package com.example.notification.application.service.notification.helper.template;

import com.example.notification.application.service.notification.data.request.SendBulletinPublishedNotificationRequest;

public interface EmailTemplateService {
    String buildBulletinPublishedEmail(SendBulletinPublishedNotificationRequest request);
}
