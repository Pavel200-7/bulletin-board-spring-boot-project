package com.example.notification.domain.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
    BULLETIN_PUBLISHED(PublisherType.USER),


    TEST_USER_NOTIFICATION(PublisherType.USER),
    TEST_SYSTEM_NOTIFICATION(PublisherType.SYSTEM);

    private PublisherType publisherType;

    NotificationType(PublisherType publisherType) {
        this.publisherType = publisherType;
    }

}
