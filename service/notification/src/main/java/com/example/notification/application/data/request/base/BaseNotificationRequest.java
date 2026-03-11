package com.example.notification.application.data.request.base;

import com.example.notification.domain.enums.NotificationType;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
public abstract class BaseNotificationRequest {
    private UUID id;
    private UUID publisherId;
    private NotificationType type;
}
