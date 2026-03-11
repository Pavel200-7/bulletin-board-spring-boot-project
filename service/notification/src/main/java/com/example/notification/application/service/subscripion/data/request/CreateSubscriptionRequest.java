package com.example.notification.application.service.subscripion.data.request;

import jakarta.validation.constraints.NotNull;
import com.example.notification.domain.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSubscriptionRequest {
    @NotNull(message = "Publisher type is required")
    private NotificationType subscriptionType;
    private UUID publisherId;
}
