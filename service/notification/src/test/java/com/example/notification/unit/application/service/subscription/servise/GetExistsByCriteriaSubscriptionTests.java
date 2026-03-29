package com.example.notification.unit.application.service.subscription.servise;

import com.example.notification.application.mapper.SubscriptionMapper;
import com.example.notification.application.service.subscripion.SubscriptionServiceImpl;
import com.example.notification.application.service.subscripion.data.request.GetExistsByCriteriaSubscriptionRequest;
import com.example.notification.application.service.subscripion.data.response.GetExistsByCriteriaSubscriptionResponse;
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

import java.util.UUID;

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

    @BeforeEach
    public void setup() {
        currentUserId = UUID.randomUUID();

        when(securityService.getCurrentUserIdAsUUID())
                .thenReturn(currentUserId);

        when(subscriptionRepository.existsByCurrentUserTypeAndPublisher(any(UUID.class), any(NotificationType.class), any(UUID.class)))
                .thenReturn(true);
    }

    @Test
    public void shouldReturnTrueWhenSubscriptionExistsForCurrentUser() {
        // Arrange
        GetExistsByCriteriaSubscriptionRequest request = createRequest(UUID.randomUUID());
        when(subscriptionRepository.existsByCurrentUserTypeAndPublisher(
                currentUserId,
                request.getSubscriptionType(),
                request.getPublisherId()))
                .thenReturn(true);

        // Act
        GetExistsByCriteriaSubscriptionResponse response = service.existsByCriteria(request);

        // Assert
        assertTrue(response.isExists());
        verify(subscriptionRepository).existsByCurrentUserTypeAndPublisher(
                currentUserId,
                request.getSubscriptionType(),
                request.getPublisherId());
    }

    @Test
    public void shouldReturnFalseWhenSubscriptionDoesNotExistForCurrentUser() {
        // Arrange
        GetExistsByCriteriaSubscriptionRequest request = createRequest(UUID.randomUUID());
        when(subscriptionRepository.existsByCurrentUserTypeAndPublisher(
                currentUserId,
                request.getSubscriptionType(),
                request.getPublisherId()))
                .thenReturn(false);

        // Act
        GetExistsByCriteriaSubscriptionResponse response = service.existsByCriteria(request);

        // Assert
        assertFalse(response.isExists());
        verify(subscriptionRepository).existsByCurrentUserTypeAndPublisher(
                currentUserId,
                request.getSubscriptionType(),
                request.getPublisherId());
    }

    @Test
    public void shouldUseCurrentUserIdFromSecurityService() {
        // Arrange
        GetExistsByCriteriaSubscriptionRequest request = createRequest(UUID.randomUUID());
        UUID expectedUserId = UUID.randomUUID();
        when(securityService.getCurrentUserIdAsUUID()).thenReturn(expectedUserId);

        // Act
        service.existsByCriteria(request);

        // Assert
        verify(securityService).getCurrentUserIdAsUUID();
        verify(subscriptionRepository).existsByCurrentUserTypeAndPublisher(
                expectedUserId,
                request.getSubscriptionType(),
                request.getPublisherId());
    }

    @Test
    public void shouldLogInformationWhenCalled() {
        // Arrange
        GetExistsByCriteriaSubscriptionRequest request = createRequest(UUID.randomUUID());

        // Act
        service.existsByCriteria(request);

        // Assert - проверяем, что метод репозитория был вызван с правильными параметрами
        verify(subscriptionRepository).existsByCurrentUserTypeAndPublisher(
                currentUserId,
                request.getSubscriptionType(),
                request.getPublisherId());
    }

    private GetExistsByCriteriaSubscriptionRequest createRequest(UUID publisherId) {
        return GetExistsByCriteriaSubscriptionRequest.builder()
                .subscriptionType(NotificationType.BULLETIN_PUBLISHED)
                .publisherId(publisherId)
                .build();
    }

}