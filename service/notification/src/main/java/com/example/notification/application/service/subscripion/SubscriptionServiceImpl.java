package com.example.notification.application.service.subscripion;

import com.example.notification.application.data.response.SubscriptionResponse;
import com.example.notification.application.exception.AccessDeniedException;
import com.example.notification.application.exception.ResourceNotFoundException;
import com.example.notification.application.mapper.SubscriptionMapper;
import com.example.notification.application.service.subscripion.data.request.CreateSubscriptionRequest;
import com.example.notification.application.service.subscripion.data.request.DeleteSubscriptionRequest;
import com.example.notification.application.service.subscripion.data.request.GetExistsByCriteriaSubscriptionRequest;
import com.example.notification.application.service.subscripion.data.request.GetSubscriptionsRequest;
import com.example.notification.application.service.subscripion.data.response.CreateSubscriptionResponse;
import com.example.notification.application.service.subscripion.data.response.DeleteSubscriptionResponse;
import com.example.notification.application.service.subscripion.data.response.GetExistsByCriteriaSubscriptionResponse;
import com.example.notification.application.service.subscripion.data.response.GetSubscriptionsResponse;
import com.example.notification.domain.entity.Subscription;
import com.example.notification.domain.entity.base.OwnerInfo;
import com.example.notification.domain.entity.base.user.User;
import com.example.notification.infrastructure.repository.SubscriptionRepository;
import com.example.notification.infrastructure.repository.UserRepository;
import com.example.notification.infrastructure.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final SubscriptionMapper subscriptionMapper;

    @Override
    @Transactional(readOnly = true)
    public GetSubscriptionsResponse getSubscriptions(GetSubscriptionsRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        List<Subscription> subscriptions = subscriptionRepository.findByOwnerInfo_Owner_Id(currentUserId);

        log.info("Found {} subscriptions for user", subscriptions.size());
        List<SubscriptionResponse> responses = subscriptions.stream()
                .map(subscriptionMapper::toResponse)
                .collect(Collectors.toList());
        return new GetSubscriptionsResponse(responses);
    }

    @Override
    @Transactional(readOnly = true)
    public GetExistsByCriteriaSubscriptionResponse existsByCriteria(GetExistsByCriteriaSubscriptionRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();

        boolean exists = subscriptionRepository.existsByCurrentUserTypeAndPublisher(
                currentUserId,
                request.getSubscriptionType(),
                request.getPublisherId()
        );

        log.info("Subscription {} у пользователя с Id: {}",
                exists ? "есть" : "отсутствует",
                currentUserId);

        return new GetExistsByCriteriaSubscriptionResponse(exists);
    }

    @Override
    @Transactional
    public CreateSubscriptionResponse createSubscription(CreateSubscriptionRequest request) {
        User currentUser = getCurrentUser();
        if (subscriptionRepository.existsByOwnerTypeAndPublisher(
                currentUser.getId(),
                request.getSubscriptionType(),
                request.getPublisherId())) {
            throw new IllegalStateException("The user is already have this subscription.");
        }

        validatePublisher(request);

        OwnerInfo ownerInfo = new OwnerInfo(currentUser);
        Subscription subscription = Subscription.createSubscription(
                ownerInfo,
                request.getSubscriptionType(),
                request.getPublisherId()
        );
        subscriptionRepository.save(subscription);
        log.info("Subscription created with id: {}", subscription.getId());

        SubscriptionResponse response = subscriptionMapper.toResponse(subscription);
        return new CreateSubscriptionResponse(response);
    }

    @Override
    @Transactional
    public DeleteSubscriptionResponse deleteSubscription(DeleteSubscriptionRequest request) {
        Subscription subscription = subscriptionRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + request.getId()));

        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        if (!subscription.getOwnerInfo().getOwnerId().equals(currentUserId)) {
            throw new AccessDeniedException("You can only delete your own subscriptions");
        }

        subscriptionRepository.delete(subscription);
        log.info("Subscription deleted successfully");

        SubscriptionResponse response = subscriptionMapper.toResponse(subscription);
        return new DeleteSubscriptionResponse(response);
    }

    private User getCurrentUser() {
        UUID userId = securityService.getCurrentUserIdAsUUID();
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private void validatePublisher(CreateSubscriptionRequest request) {
        if (request.getPublisherId() != null) {
            if (!userRepository.existsById(request.getPublisherId())) {
                throw new ResourceNotFoundException("Publisher user not found with id: " + request.getPublisherId());
            }
        }
    }

}