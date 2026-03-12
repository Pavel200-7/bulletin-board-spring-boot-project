package com.example.notification.application.service.notification.helper.iterator;

import com.example.notification.domain.entity.Subscription;
import com.example.notification.domain.enums.NotificationType;
import com.example.notification.infrastructure.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class SubscriptionIterator implements Iterator<Subscription> {

    private final SubscriptionRepository repository;
    private final NotificationType notificationType;
    private final UUID publisherId;
    private final int pageSize;

    private int pageNumber = 0;
    private Iterator<Subscription> currentPage = Collections.emptyIterator();
    private boolean hasNextPages = true;

    @Override
    public boolean hasNext() {
        if (currentPage.hasNext()) {
            return true;
        }
        if (hasNextPages) {
            loadNextPage();
            return currentPage.hasNext();
        }
        return false;
    }

    @Override
    public Subscription next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return currentPage.next();
    }

    private void loadNextPage() {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Subscription> page = repository
                .findPageByTypeAndPublisher(notificationType, publisherId, pageable);
        log.info("Было найдено {} подписчиков в этой итерации.", page.stream().count());

        currentPage = page.getContent().iterator();
        hasNextPages = page.hasNext();
        pageNumber++;
    }

}
