package com.example.notification.application.service.subscripion.data.response;

import com.example.notification.application.data.response.SubscriptionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSubscriptionResponse {
    private SubscriptionResponse subscriptionResponse;
}
