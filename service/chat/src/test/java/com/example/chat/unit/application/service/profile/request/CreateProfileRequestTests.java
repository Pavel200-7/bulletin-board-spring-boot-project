package com.example.chat.unit.application.service.profile.request;

import com.example.chat.application.service.profile.data.request.CreateProfileRequest;
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
public class CreateProfileRequestTests {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldApproveWhenValid() {
        // Arrange
        CreateProfileRequest request = CreateProfileRequest.builder()
                .ownerId(UUID.randomUUID())
                .ownerName("Test User")
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    public void shouldForbidNullOwnerId(UUID ownerId) {
        // Arrange
        CreateProfileRequest request = CreateProfileRequest.builder()
                .ownerId(ownerId)
                .ownerName("Test User")
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldAllowNullOwnerName() {
        // Arrange
        CreateProfileRequest request = CreateProfileRequest.builder()
                .ownerId(UUID.randomUUID())
                .ownerName(null)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

}