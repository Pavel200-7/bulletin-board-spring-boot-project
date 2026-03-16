package com.example.chat.unit.application.service.message.text.data.request;

import com.example.chat.application.service.message.text.data.request.CreateTextChatMessageRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class CreateTextChatMessageRequestTests {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationWhenCreateRequestIsValid() {
        // Arrange
        CreateTextChatMessageRequest request = CreateTextChatMessageRequest.builder()
                .chatId(UUID.randomUUID())
                .text("Hello, world!")
                .build();

        // Act
        Set<ConstraintViolation<CreateTextChatMessageRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    void shouldFailValidationWhenChatIdIsNull(UUID nullChatId) {
        // Arrange
        CreateTextChatMessageRequest request = CreateTextChatMessageRequest.builder()
                .chatId(nullChatId)
                .text("Hello, world!")
                .build();

        // Act
        Set<ConstraintViolation<CreateTextChatMessageRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("chatId", violations.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldFailValidationWhenTextIsBlank(String invalidText) {
        // Arrange
        CreateTextChatMessageRequest request = CreateTextChatMessageRequest.builder()
                .chatId(UUID.randomUUID())
                .text(invalidText)
                .build();

        // Act
        Set<ConstraintViolation<CreateTextChatMessageRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("text", violations.iterator().next().getPropertyPath().toString());
    }

}
