package com.example.chat.unit.application.service.message.image.data.request;

import com.example.chat.application.service.message.image.data.request.CreateImageChatMessageRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class CreateImageChatMessageRequestTests {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationWhenAllFieldsAreValid() {
        // Arrange
        CreateImageChatMessageRequest request = CreateImageChatMessageRequest.builder()
                .chatId(UUID.randomUUID())
                .imageId(UUID.randomUUID())
                .build();

        // Act
        Set<ConstraintViolation<CreateImageChatMessageRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    void shouldFailValidationWhenChatIdIsNull(UUID nullChatId) {
        // Arrange
        CreateImageChatMessageRequest request = CreateImageChatMessageRequest.builder()
                .chatId(nullChatId)
                .imageId(UUID.randomUUID())
                .build();

        // Act
        Set<ConstraintViolation<CreateImageChatMessageRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<CreateImageChatMessageRequest> violation = violations.iterator().next();
        assertEquals("chatId", violation.getPropertyPath().toString());
        assertNotNull(violation.getMessage());
    }

    @ParameterizedTest
    @NullSource
    void shouldFailValidationWhenImageIdIsNull(UUID nullImageId) {
        // Arrange
        CreateImageChatMessageRequest request = CreateImageChatMessageRequest.builder()
                .chatId(UUID.randomUUID())
                .imageId(nullImageId)
                .build();

        // Act
        Set<ConstraintViolation<CreateImageChatMessageRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<CreateImageChatMessageRequest> violation = violations.iterator().next();
        assertEquals("imageId", violation.getPropertyPath().toString());
        assertNotNull(violation.getMessage());
    }

}