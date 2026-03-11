package com.example.notification.application.service.subscripion.data.request;

import com.example.notification.application.data.response.SubscriptionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteSubscriptionRequest {
    private UUID id;
}
