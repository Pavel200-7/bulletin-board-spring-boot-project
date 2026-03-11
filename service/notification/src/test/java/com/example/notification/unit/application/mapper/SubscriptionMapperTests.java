package com.example.notification.unit.application.mapper;

import com.example.notification.application.data.response.SubscriptionResponse;
import com.example.notification.application.mapper.SubscriptionMapper;
import com.example.notification.domain.entity.Subscription;
import com.example.notification.domain.entity.base.OwnerInfo;
import com.example.notification.domain.entity.base.user.User;
import com.example.notification.domain.enums.NotificationType;
import com.example.notification.domain.enums.PublisherType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class SubscriptionMapperTests {

    private SubscriptionMapper mapper = Mappers.getMapper(
            SubscriptionMapper.class);

    @Test
    public void shouldConvertCorrectlyFromEntityToResponse() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID publisherId = UUID.randomUUID();

        User user = User.createUser(userId, "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);

        Subscription subscription = Subscription.createSubscription(
                ownerInfo,
                NotificationType.TEST_USER_NOTIFICATION,
                publisherId);

        SubscriptionResponse expected = SubscriptionResponse.builder()
                .id(subscription.getId())
                .ownerId(userId)
                .type(NotificationType.TEST_USER_NOTIFICATION)
                .publisherType(PublisherType.USER)
                .publisherId(publisherId)
                .build();

        // Act
        SubscriptionResponse actual = mapper.toResponse(subscription);

        // Assert
        assertTrue(expected.equalsData(actual));
    }

    @Test
    public void shouldConvertWithSystemPublisher() {
        // Arrange
        UUID userId = UUID.randomUUID();

        User user = User.createUser(userId, "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);

        Subscription subscription = Subscription.createSubscription(
                ownerInfo,
                NotificationType.TEST_SYSTEM_NOTIFICATION, null);

        SubscriptionResponse expected = SubscriptionResponse.builder()
                .id(subscription.getId())
                .ownerId(userId)
                .type(NotificationType.TEST_SYSTEM_NOTIFICATION)
                .publisherType(PublisherType.SYSTEM)
                .publisherId(null)
                .build();

        // Act
        SubscriptionResponse actual = mapper.toResponse(subscription);

        // Assert
        assertTrue(expected.equalsData(actual));
    }

}