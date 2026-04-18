package com.example.notification.unit.application.service.subscription.servise;

import com.example.notification.application.mapper.SubscriptionMapper;
import com.example.notification.application.service.subscripion.SubscriptionServiceImpl;
import com.example.notification.application.service.subscripion.data.request.GetSubscriptionsRequest;
import com.example.notification.application.service.subscripion.data.response.GetSubscriptionsResponse;
import com.example.notification.domain.entity.Subscription;
import com.example.notification.domain.entity.base.OwnerInfo;
import com.example.notification.domain.entity.base.user.User;
import com.example.notification.domain.enums.NotificationType;
import com.example.notification.infrastructure.repository.SubscriptionRepository;
import com.example.notification.infrastructure.security.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GetSubscriptionsTests {

    private SubscriptionMapper mapperHelper = Mappers.getMapper(
            SubscriptionMapper.class);

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private SubscriptionMapper mapper;

    @InjectMocks
    private SubscriptionServiceImpl service;

    private User currentUser;
    private UUID currentUserId;
    private OwnerInfo ownerInfo;

    @BeforeEach
    public void setup() {
        currentUserId = UUID.randomUUID();
        currentUser = User.createUser(currentUserId, "test@example.com");
        ownerInfo = new OwnerInfo(currentUser);

        when(securityService.getCurrentUserIdAsUUID())
                .thenReturn(currentUserId);

        when(mapper.toResponse(any(Subscription.class)))
                .thenAnswer(invocation -> {
                    Subscription subscription = invocation.getArgument(0);
                    return mapperHelper.toResponse(subscription);
                });
    }

    @Test
    public void shouldGetUserSubscriptions() {
        // Arrange
        Subscription sub1 = createSubscription();
        Subscription sub2 = createSubscription();

        when(subscriptionRepository.findByOwnerInfoOwnerId(any(UUID.class)))
                .thenReturn(List.of(sub1, sub2));

        // Act
        GetSubscriptionsResponse response = service.getSubscriptions(new GetSubscriptionsRequest());

        // Assert
        assertNotNull(response);
        assertNotNull(response.getSubscriptionResponses());
        assertEquals(2, response.getSubscriptionResponses().size());
    }

    @Test
    public void shouldReturnEmptyListWhenNoSubscriptions() {
        // Arrange
        when(subscriptionRepository.findByOwnerInfoOwnerId(any(UUID.class)))
                .thenReturn(List.of());

        // Act
        GetSubscriptionsResponse response = service.getSubscriptions(new GetSubscriptionsRequest());

        // Assert
        assertNotNull(response);
        assertNotNull(response.getSubscriptionResponses());
        assertTrue(response.getSubscriptionResponses().isEmpty());
    }

    private Subscription createSubscription() {
        return Subscription.createSubscription(ownerInfo, NotificationType.TEST_USER_NOTIFICATION, UUID.randomUUID());
    }

}