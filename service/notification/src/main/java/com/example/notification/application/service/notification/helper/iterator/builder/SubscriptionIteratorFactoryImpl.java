package com.example.notification.application.service.notification.helper.iterator.builder;

import com.example.notification.application.service.notification.helper.iterator.SubscriptionIterable;
import com.example.notification.domain.entity.Subscription;
import com.example.notification.domain.enums.NotificationType;
import com.example.notification.infrastructure.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SubscriptionIteratorFactoryImpl implements SubscriptionIteratorFactory {

    private final int PAGE_SIZE = 100;
    private final SubscriptionRepository repository;

    public Iterable<Subscription> createSubscriptionIterator(NotificationType notificationType, UUID publisherId) {
        return new SubscriptionIterable(repository, notificationType, publisherId, PAGE_SIZE);
    }
}
