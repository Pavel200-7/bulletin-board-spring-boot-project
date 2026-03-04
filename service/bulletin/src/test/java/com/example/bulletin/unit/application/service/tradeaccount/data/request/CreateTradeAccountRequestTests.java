package com.example.bulletin.unit.application.service.tradeaccount.data.request;

import com.example.bulletin.application.service.tradeaccount.data.request.CreateTradeAccountRequest;
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
public class CreateTradeAccountRequestTests {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldApproveWhenValid() {
        // Arrange
        CreateTradeAccountRequest request = createValidRequestBuilder()
                .build();
        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    public void shouldForbidBlankId(UUID id) {
        // Arrange
        CreateTradeAccountRequest request = createValidRequestBuilder()
                .ownerId(id)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    public CreateTradeAccountRequest.CreateTradeAccountRequestBuilder createValidRequestBuilder() {
        return CreateTradeAccountRequest.builder()
                .ownerId(UUID.randomUUID());
    }

}
