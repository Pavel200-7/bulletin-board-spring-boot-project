package com.example.notification.unit.application.service.subscription.data.request;

import com.example.notification.application.service.subscripion.data.request.GetExistsByCriteriaSubscriptionRequest;
import com.example.notification.domain.enums.NotificationType;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class GetExistsByCriteriaSubscriptionRequestTests {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldApproveWhenValid() {
        // Arrange
        GetExistsByCriteriaSubscriptionRequest request = createValidRequestBuilder()
                .build();
        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldForbidNullSubscriptionType() {
        // Arrange
        GetExistsByCriteriaSubscriptionRequest request = createValidRequestBuilder()
                .subscriptionType(null)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldForbidNullPublisherId() {
        // Arrange
        GetExistsByCriteriaSubscriptionRequest request = createValidRequestBuilder()
                .publisherId(null)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    public GetExistsByCriteriaSubscriptionRequest.GetExistsByCriteriaSubscriptionRequestBuilder createValidRequestBuilder() {
        return GetExistsByCriteriaSubscriptionRequest.builder()
                .subscriptionType(NotificationType.BULLETIN_PUBLISHED)
                .publisherId(UUID.randomUUID());
    }

}
