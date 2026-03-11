package com.example.notification.unit.application.service.subscription.servise;

import com.example.notification.application.data.response.SubscriptionResponse;
import com.example.notification.application.exception.ResourceNotFoundException;
import com.example.notification.application.mapper.SubscriptionMapper;
import com.example.notification.application.service.subscripion.SubscriptionServiceImpl;
import com.example.notification.application.service.subscripion.data.request.CreateSubscriptionRequest;
import com.example.notification.application.service.subscripion.data.response.CreateSubscriptionResponse;
import com.example.notification.domain.entity.Subscription;
import com.example.notification.domain.entity.base.OwnerInfo;
import com.example.notification.domain.entity.base.user.User;
import com.example.notification.domain.enums.NotificationType;
import com.example.notification.domain.enums.PublisherType;
import com.example.notification.infrastructure.repository.SubscriptionRepository;
import com.example.notification.infrastructure.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CreateSubscriptionTests {

    private SubscriptionMapper mapperHelper = Mappers.getMapper(
            SubscriptionMapper.class);

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

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

    @BeforeEach
    public void setup() {
        currentUserId = UUID.randomUUID();
        currentUser = User.createUser(currentUserId, "test@example.com");
        ownerInfo = new OwnerInfo(currentUser);

        when(securityService.getCurrentUserIdAsUUID())
                .thenReturn(currentUserId);

        when(userRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(currentUser));

        when(userRepository.existsById(any(UUID.class)))
                .thenReturn(true);

        when(subscriptionRepository.existsByOwnerTypeAndPublisher(any(UUID.class), any(NotificationType.class), any(UUID.class)))
                .thenReturn(false);

        when(subscriptionRepository.save(any(Subscription.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(mapper.toResponse(any(Subscription.class)))
                .thenAnswer(invocation -> {
                    Subscription subscription = invocation.getArgument(0);
                    return mapperHelper.toResponse(subscription);
                });
    }

    @Test
    public void shouldCreateUserSubscription() {
        // Arrange
        UUID publisherId = UUID.randomUUID();
        when(userRepository.existsById(publisherId)).thenReturn(true);

        CreateSubscriptionRequest request = CreateSubscriptionRequest.builder()
                .subscriptionType(NotificationType.TEST_USER_NOTIFICATION)
                .publisherId(publisherId)
                .build();

        // Act
        CreateSubscriptionResponse response = service.createSubscription(request);

        // Assert
        verify(subscriptionRepository).save(subscriptionCaptor.capture());

        SubscriptionResponse responseData = response.getSubscriptionResponse();
        assertNotNull(responseData);
        assertEquals(currentUserId, responseData.getOwnerId());
        assertEquals(NotificationType.TEST_USER_NOTIFICATION, responseData.getType());
        assertEquals(PublisherType.USER, responseData.getPublisherType());
        assertEquals(publisherId, responseData.getPublisherId());
    }

    @Test
    public void shouldCreateSystemSubscription() {
        // Arrange
        UUID publisherId = UUID.randomUUID();
        CreateSubscriptionRequest request = CreateSubscriptionRequest.builder()
                .subscriptionType(NotificationType.TEST_SYSTEM_NOTIFICATION)
                .publisherId(publisherId)
                .build();

        // Act
        CreateSubscriptionResponse response = service.createSubscription(request);

        // Assert
        verify(subscriptionRepository).save(subscriptionCaptor.capture());

        SubscriptionResponse responseData = response.getSubscriptionResponse();
        assertNotNull(responseData);
        assertEquals(currentUserId, responseData.getOwnerId());
        assertEquals(NotificationType.TEST_SYSTEM_NOTIFICATION, responseData.getType());
        assertEquals(PublisherType.SYSTEM, responseData.getPublisherType());
        assertNull(responseData.getPublisherId());
    }

    @Test
    public void shouldThrowWhenUserNotFound() {
        // Arrange
        when(userRepository.findById(currentUserId)).thenReturn(Optional.empty());

        CreateSubscriptionRequest request = CreateSubscriptionRequest.builder()
                .subscriptionType(NotificationType.TEST_SYSTEM_NOTIFICATION)
                .publisherId(UUID.randomUUID())
                .build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> service.createSubscription(request));
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    public void shouldThrowWhenPublisherNotFound() {
        // Arrange
        UUID publisherId = UUID.randomUUID();
        when(userRepository.existsById(publisherId)).thenReturn(false);

        CreateSubscriptionRequest request = CreateSubscriptionRequest.builder()
                .subscriptionType(NotificationType.TEST_SYSTEM_NOTIFICATION)
                .publisherId(publisherId)
                .build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> service.createSubscription(request));
    }

    @Test
    public void shouldThrowWhenSubscriptionExistsForThisUser() {
        // Arrange
        UUID publisherId = UUID.randomUUID();
        CreateSubscriptionRequest request = CreateSubscriptionRequest.builder()
                .subscriptionType(NotificationType.TEST_SYSTEM_NOTIFICATION)
                .publisherId(publisherId)
                .build();

        when(subscriptionRepository.existsByOwnerTypeAndPublisher(any(UUID.class), any(NotificationType.class), any(UUID.class)))
                .thenReturn(true);

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> service.createSubscription(request));
    }

}
