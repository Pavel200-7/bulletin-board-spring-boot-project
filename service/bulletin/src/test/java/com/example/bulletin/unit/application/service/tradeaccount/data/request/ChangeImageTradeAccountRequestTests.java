package com.example.bulletin.unit.application.service.tradeaccount.data.request;

import com.example.bulletin.application.service.tradeaccount.data.request.ChangeImageTradeAccountRequest;
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
public class ChangeImageTradeAccountRequestTests {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldApproveWhenValid() {
        // Arrange
        ChangeImageTradeAccountRequest request = createValidRequestBuilder()
                .build();
        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldForbidNullImageId() {
        // Arrange
        ChangeImageTradeAccountRequest request = createValidRequestBuilder()
                .imageId(null)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    public ChangeImageTradeAccountRequest.ChangeImageTradeAccountRequestBuilder createValidRequestBuilder() {
        return ChangeImageTradeAccountRequest.builder()
                .imageId(UUID.randomUUID());
    }

}