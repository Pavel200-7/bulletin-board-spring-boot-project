package com.example.notification.unit.application.service.subscription.servise;

import com.example.notification.application.data.response.SubscriptionResponse;
import com.example.notification.application.mapper.SubscriptionMapper;
import com.example.notification.application.service.subscripion.SubscriptionServiceImpl;
import com.example.notification.application.service.subscripion.data.request.GetExistsByCriteriaSubscriptionRequest;
import com.example.notification.application.service.subscripion.data.response.GetExistsByCriteriaSubscriptionResponse;
import com.example.notification.domain.entity.Subscription;
import com.example.notification.domain.entity.base.OwnerInfo;
import com.example.notification.domain.entity.base.user.User;
import com.example.notification.domain.enums.NotificationType;
import com.example.notification.infrastructure.repository.SubscriptionRepository;
import com.example.notification.infrastructure.repository.UserRepository;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GetExistsByCriteriaSubscriptionTests {

    private SubscriptionMapper mapperHelper = Mappers.getMapper(
            SubscriptionMapper.class);

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscriptionMapper mapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private SubscriptionServiceImpl service;

    private UUID currentUserId;
    private Subscription testSubscription;

    @BeforeEach
    public void setup() {
        currentUserId = UUID.randomUUID();

        when(securityService.getCurrentUserIdAsUUID())
                .thenReturn(currentUserId);

        // Создаем тестовую подписку
        User user = User.createUser(currentUserId, "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        testSubscription = Subscription.createSubscription(
                ownerInfo,
                NotificationType.BULLETIN_PUBLISHED,
                UUID.randomUUID()
        );

        when(mapper.toResponse(any(Subscription.class)))
                .thenAnswer(invocation -> {
                    Subscription subscription = invocation.getArgument(0);
                    return mapperHelper.toResponse(subscription);
                });
    }

    @Test
    public void shouldReturnExistsTrueAndSubscriptionWhenSubscriptionExists() {
        // Arrange
        GetExistsByCriteriaSubscriptionRequest request = createRequest();
        when(subscriptionRepository.findByCurrentUserTypeAndPublisher(
                currentUserId,
                request.getSubscriptionType(),
                request.getPublisherId()))
                .thenReturn(Optional.of(testSubscription));

        SubscriptionResponse expectedResponse = mapperHelper.toResponse(testSubscription);

        // Act
        GetExistsByCriteriaSubscriptionResponse response = service.existsByCriteria(request);

        // Assert
        assertTrue(response.isExists());
        assertThat(response.getSubscriptionResponse())
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(subscriptionRepository).findByCurrentUserTypeAndPublisher(
                currentUserId,
                request.getSubscriptionType(),
                request.getPublisherId());
    }

    @Test
    public void shouldReturnExistsFalseAndNullSubscriptionWhenSubscriptionDoesNotExist() {
        // Arrange
        GetExistsByCriteriaSubscriptionRequest request = createRequest();
        when(subscriptionRepository.findByCurrentUserTypeAndPublisher(
                currentUserId,
                request.getSubscriptionType(),
                request.getPublisherId()))
                .thenReturn(Optional.empty());

        // Act
        GetExistsByCriteriaSubscriptionResponse response = service.existsByCriteria(request);

        // Assert
        assertFalse(response.isExists());
        assertNull(response.getSubscriptionResponse());

        verify(subscriptionRepository).findByCurrentUserTypeAndPublisher(
                currentUserId,
                request.getSubscriptionType(),
                request.getPublisherId());
    }

    @Test
    public void shouldUseCurrentUserIdFromSecurityService() {
        // Arrange
        GetExistsByCriteriaSubscriptionRequest request = createRequest();
        UUID expectedUserId = UUID.randomUUID();
        when(securityService.getCurrentUserIdAsUUID()).thenReturn(expectedUserId);
        when(subscriptionRepository.findByCurrentUserTypeAndPublisher(
                expectedUserId,
                request.getSubscriptionType(),
                request.getPublisherId()))
                .thenReturn(Optional.empty());

        // Act
        service.existsByCriteria(request);

        // Assert
        verify(securityService).getCurrentUserIdAsUUID();
        verify(subscriptionRepository).findByCurrentUserTypeAndPublisher(
                expectedUserId,
                request.getSubscriptionType(),
                request.getPublisherId());
    }

    private GetExistsByCriteriaSubscriptionRequest createRequest() {
        return GetExistsByCriteriaSubscriptionRequest.builder()
                .subscriptionType(NotificationType.BULLETIN_PUBLISHED)
                .publisherId(UUID.randomUUID())
                .build();
    }
}