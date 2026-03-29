package com.example.notification.application.service.subscripion.data.request;

import com.example.notification.domain.enums.NotificationType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetExistsByCriteriaSubscriptionRequest {
    @NotNull(message = "Publisher type is required")
    private NotificationType subscriptionType;
    @NotNull
    private UUID publisherId;
}
