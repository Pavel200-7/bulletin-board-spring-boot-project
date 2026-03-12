package com.example.notification.application.service.notification.helper.iterator;

import com.example.notification.domain.entity.Subscription;
import com.example.notification.domain.enums.NotificationType;
import com.example.notification.infrastructure.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.UUID;

@RequiredArgsConstructor
public class SubscriptionIterable implements Iterable<Subscription> {

    private final SubscriptionRepository repository;
    private final NotificationType notificationType;
    private final UUID publisherId;
    private final int pageSize;

    @Override
    public Iterator<Subscription> iterator() {
        return new SubscriptionIterator(repository, notificationType, publisherId, pageSize);
    }

}
