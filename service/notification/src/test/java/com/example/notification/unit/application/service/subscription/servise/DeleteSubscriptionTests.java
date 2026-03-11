package com.example.notification.unit.application.service.subscription.servise;

import com.example.notification.application.data.response.SubscriptionResponse;
import com.example.notification.application.exception.AccessDeniedException;
import com.example.notification.application.exception.ResourceNotFoundException;
import com.example.notification.application.mapper.SubscriptionMapper;
import com.example.notification.application.service.subscripion.SubscriptionServiceImpl;
import com.example.notification.application.service.subscripion.data.request.DeleteSubscriptionRequest;
import com.example.notification.application.service.subscripion.data.response.DeleteSubscriptionResponse;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DeleteSubscriptionTests {

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

    @Captor
    private ArgumentCaptor<Subscription> subscriptionCaptor;

    private User currentUser;
    private UUID currentUserId;
    private OwnerInfo ownerInfo;
    private Subscription subscription;

    @BeforeEach
    public void setup() {
        currentUserId = UUID.randomUUID();
        currentUser = User.createUser(currentUserId, "test@example.com");
        ownerInfo = new OwnerInfo(currentUser);

        subscription = createSubscription();

        when(securityService.getCurrentUserIdAsUUID())
                .thenReturn(currentUserId);

        when(subscriptionRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(subscription));

        doAnswer(invocation -> null).when(subscriptionRepository).delete(any(Subscription.class));

        when(mapper.toResponse(any(Subscription.class)))
                .thenAnswer(invocation -> {
                    Subscription sub = invocation.getArgument(0);
                    return mapperHelper.toResponse(sub);
                });
    }

    @Test
    public void shouldDeleteOwnSubscription() {
        // Arrange
        DeleteSubscriptionRequest request = new DeleteSubscriptionRequest(subscription.getId());

        // Act
        DeleteSubscriptionResponse response = service.deleteSubscription(request);

        // Assert
        verify(subscriptionRepository).delete(subscriptionCaptor.capture());
        Subscription actual = subscriptionCaptor.getValue();
        assertEquals(subscription.getId(), actual.getId());

        SubscriptionResponse responseData = response.getSubscriptionResponse();
        assertNotNull(responseData);
        assertEquals(currentUserId, responseData.getOwnerId());
    }

    @Test
    public void shouldThrowWhenSubscriptionNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        DeleteSubscriptionRequest request = new DeleteSubscriptionRequest(nonExistentId);

        when(subscriptionRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> service.deleteSubscription(request));
        verify(subscriptionRepository, never()).delete(any());
    }

    @Test
    public void shouldThrowWhenDeletingOthersSubscription() {
        // Arrange
        Subscription otherSubscription = createSubscription();

        when(securityService.getCurrentUserIdAsUUID())
                .thenReturn(UUID.randomUUID());

        DeleteSubscriptionRequest request = new DeleteSubscriptionRequest(otherSubscription.getId());

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> service.deleteSubscription(request));
    }

    private Subscription createSubscription() {
        return Subscription.createSubscription(ownerInfo, NotificationType.TEST_USER_NOTIFICATION, UUID.randomUUID());
    }

}