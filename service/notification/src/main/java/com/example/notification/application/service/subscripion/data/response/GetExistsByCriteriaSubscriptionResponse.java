package com.example.notification.application.service.subscripion.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetExistsByCriteriaSubscriptionResponse {
    private boolean exists;
}
