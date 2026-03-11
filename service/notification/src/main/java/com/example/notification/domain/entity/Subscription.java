package com.example.notification.domain.entity;

import com.example.notification.domain.entity.base.BaseEntity;
import com.example.notification.domain.entity.base.OwnerInfo;
import com.example.notification.domain.entity.base.user.User;
import com.example.notification.domain.enums.PublisherType;
import com.example.notification.domain.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

import java.util.UUID;

@Entity
@Getter
@Table(name = "subscriptions")
public class Subscription extends BaseEntity {

    @Id
    @Column(name = "id")
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Embedded
    @Delegate
    @Setter(AccessLevel.NONE)
    private OwnerInfo ownerInfo;

    @Enumerated(value = EnumType.ORDINAL)
    @Column(name = "type")
    private NotificationType type;

    @Enumerated(value = EnumType.ORDINAL)
    @Column(name = "publisher_type", nullable = false)
    @Setter(AccessLevel.NONE)
    private PublisherType publisherType;

    @Column(name = "publisher_id", nullable = true)
    @Setter(AccessLevel.NONE)
    private UUID publisherId;

    protected Subscription() {}

    private Subscription(OwnerInfo ownerInfo, NotificationType type, PublisherType publisherType, UUID publisherId) {
        this.id = UUID.randomUUID();
        this.ownerInfo = ownerInfo;
        this.type = type;
        this.publisherType = publisherType;
        this.publisherId = publisherId;
    }

    public static Subscription createSubscription(OwnerInfo ownerInfo, NotificationType type, UUID publisherId) {
        if (type.getPublisherType().equals(PublisherType.SYSTEM)) {
            return new Subscription(ownerInfo, type, PublisherType.SYSTEM, null);
        }

        if (publisherId == null) {
            throw new IllegalStateException("You should chose publisher for this subscription");
        }
        return new Subscription(ownerInfo, type, PublisherType.USER, publisherId);
    }

}
