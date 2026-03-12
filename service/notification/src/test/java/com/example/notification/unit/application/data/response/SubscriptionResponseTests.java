package com.example.notification.unit.application.data.response;

import com.example.notification.application.data.response.SubscriptionResponse;
import com.example.notification.domain.enums.NotificationType;
import com.example.notification.domain.enums.PublisherType;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class SubscriptionResponseTests {

    @Test
    void shouldReturnTrueWhenAllDataFieldsMatch() {
        // Arrange
        SubscriptionResponse data1 = createResponseBuilder()
                .build();

        SubscriptionResponse data2 = createResponseBuilder()
                .build();

        // Act
        boolean result = data1.equalsData(data2);

        // Assert
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenOwnerIdDiffers() {
        // Arrange
        SubscriptionResponse data1 = createResponseBuilder()
                .ownerId(UUID.randomUUID())
                .build();

        SubscriptionResponse data2 = createResponseBuilder()
                .ownerId(UUID.randomUUID())
                .build();

        // Act
        boolean result = data1.equalsData(data2);

        // Assert
        assertFalse(result);
    }

    private SubscriptionResponse.SubscriptionResponseBuilder createResponseBuilder() {
        UUID ownerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID publisherId = UUID.fromString("11111111-1111-1111-1111-111111111112");
        return SubscriptionResponse.builder()
                .id(UUID.randomUUID())
                .ownerId(ownerId)
                .type(NotificationType.BULLETIN_PUBLISHED)
                .publisherType(PublisherType.USER)
                .publisherId(publisherId);
    }

}