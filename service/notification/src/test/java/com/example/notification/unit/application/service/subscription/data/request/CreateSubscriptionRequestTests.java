package com.example.notification.unit.application.service.subscription.data.request;

import com.example.notification.application.service.subscripion.data.request.CreateSubscriptionRequest;
import com.example.notification.domain.enums.NotificationType;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class CreateSubscriptionRequestTests {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldApproveWhenValid() {
        // Arrange
        CreateSubscriptionRequest request = createValidRequestBuilder()
                .build();
        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    public void shouldForbidBlankSubscriptionType(NotificationType subscriptionType) {
        // Arrange
        CreateSubscriptionRequest request = createValidRequestBuilder()
                .subscriptionType(subscriptionType)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    public CreateSubscriptionRequest.CreateSubscriptionRequestBuilder createValidRequestBuilder() {
        return CreateSubscriptionRequest.builder()
                .subscriptionType(NotificationType.TEST_USER_NOTIFICATION)
                .publisherId(UUID.randomUUID());
    }

}
