package com.example.notification.application.data.response;

import com.example.notification.domain.enums.NotificationType;
import com.example.notification.domain.enums.PublisherType;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;
import java.util.UUID;

@Value
@Builder
public class SubscriptionResponse {
    private UUID id;
    private UUID ownerId;
    private NotificationType type;
    private PublisherType publisherType;
    private UUID publisherId;

    public boolean equalsData(SubscriptionResponse other) {
        if (other == null) return false;
        return Objects.equals(ownerId, other.ownerId) &&
                Objects.equals(type, other.type) &&
                Objects.equals(publisherType, other.publisherType) &&
                Objects.equals(publisherId, other.publisherId);
    }

}
