package com.example.notification.application.service.notification.helper.iterator.builder;

import com.example.notification.domain.entity.Subscription;
import com.example.notification.domain.enums.NotificationType;

import java.util.UUID;

public interface SubscriptionIteratorFactory {
    Iterable<Subscription> createSubscriptionIterator(NotificationType notificationType, UUID publisherId);
}
