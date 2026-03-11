package com.example.notification.unit.domain.entity.subscription;

import com.example.notification.domain.entity.Subscription;
import com.example.notification.domain.entity.base.OwnerInfo;
import com.example.notification.domain.entity.base.user.User;
import com.example.notification.domain.enums.NotificationType;
import com.example.notification.domain.enums.PublisherType;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
public class CreateSubscriptionTests {

    @Test
    public void shouldCreateUserPublisherSubscription() {
        // Arrange
        UUID publisherId = UUID.randomUUID();
        NotificationType notificationType = NotificationType.TEST_USER_NOTIFICATION;

        User user = User.createUser(UUID.randomUUID(), "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);

        // Act
        Subscription subscription = Subscription.createSubscription(ownerInfo, notificationType, publisherId);

        // Assert
        assertEquals(subscription.getOwnerInfo(), ownerInfo);
        assertEquals(subscription.getPublisherId(), publisherId);
        assertEquals(subscription.getType(), notificationType);
        assertEquals(subscription.getPublisherType(), PublisherType.USER);
    }

    @Test
    public void shouldCreateUserSubscription() {
        // Arrange
        UUID publisherId = UUID.randomUUID();
        NotificationType notificationType = NotificationType.TEST_SYSTEM_NOTIFICATION;

        User user = User.createUser(UUID.randomUUID(), "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);

        // Act
        Subscription subscription = Subscription.createSubscription(ownerInfo, notificationType, publisherId);

        // Assert
        assertEquals(subscription.getOwnerInfo(), ownerInfo);
        assertEquals(subscription.getPublisherId(), null);
        assertEquals(subscription.getType(), notificationType);
        assertEquals(subscription.getPublisherType(), PublisherType.SYSTEM);
    }

    @Test
    public void shouldThrowWhenCreateUserSubscriptionWithoutPublisherId() {
        // Arrange
        NotificationType notificationType = NotificationType.TEST_USER_NOTIFICATION;

        User user = User.createUser(UUID.randomUUID(), "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> Subscription.createSubscription(ownerInfo, notificationType, null));
    }

}
