package com.example.notification.host.controller;

import com.example.notification.application.service.subscripion.SubscriptionService;
import com.example.notification.application.service.subscripion.data.request.CreateSubscriptionRequest;
import com.example.notification.application.service.subscripion.data.request.DeleteSubscriptionRequest;
import com.example.notification.application.service.subscripion.data.request.GetSubscriptionsRequest;
import com.example.notification.application.service.subscripion.data.response.CreateSubscriptionResponse;
import com.example.notification.application.service.subscripion.data.response.DeleteSubscriptionResponse;
import com.example.notification.application.service.subscripion.data.response.GetSubscriptionsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/subscription")
public class SubscriptionController {

    private final SubscriptionService service;

    @GetMapping()
    public ResponseEntity<GetSubscriptionsResponse> getSubscriptions() {
        return ResponseEntity.ok(service.getSubscriptions(new GetSubscriptionsRequest()));
    }

    @PostMapping
    public ResponseEntity<CreateSubscriptionResponse> createSubscription(
            @Valid @RequestBody CreateSubscriptionRequest request) {
        return ResponseEntity.ok(service.createSubscription(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteSubscriptionResponse> deleteSubscription(@PathVariable UUID id) {
        DeleteSubscriptionRequest request = new DeleteSubscriptionRequest(id);
        return ResponseEntity.ok(service.deleteSubscription(request));
    }

}
