package com.example.notification.application.service.subscripion;

import com.example.notification.application.service.subscripion.data.request.*;
import com.example.notification.application.service.subscripion.data.response.*;

public interface SubscriptionService {
    GetSubscriptionsResponse getSubscriptions(GetSubscriptionsRequest request);
    CreateSubscriptionResponse createSubscription(CreateSubscriptionRequest request);
    DeleteSubscriptionResponse deleteSubscription(DeleteSubscriptionRequest request);
}
