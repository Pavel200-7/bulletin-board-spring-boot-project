package com.example.chat.unit.application.service.message.text.data.request;

import com.example.chat.application.service.message.text.data.request.CreateTextChatMessageRequest;
import com.example.chat.application.service.message.text.data.request.UpdateTextChatMessageRequest;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ActiveProfiles("test")
public class UpdateTextChatMessageRequestTests {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationWhenUpdateRequestIsValid() {
        // Arrange
        UpdateTextChatMessageRequest request = UpdateTextChatMessageRequest.builder()
                .messageId(UUID.randomUUID())
                .newText("Updated text")
                .build();

        // Act
        Set<ConstraintViolation<UpdateTextChatMessageRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    void shouldFailValidationWhenMessageIdIsNull(UUID nullMessageId) {
        // Arrange
        UpdateTextChatMessageRequest request = UpdateTextChatMessageRequest.builder()
                .messageId(nullMessageId)
                .newText("Updated text")
                .build();

        // Act
        Set<ConstraintViolation<UpdateTextChatMessageRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("messageId", violations.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldFailValidationWhenNewTextIsBlank(String invalidText) {
        // Arrange
        UpdateTextChatMessageRequest request = UpdateTextChatMessageRequest.builder()
                .messageId(UUID.randomUUID())
                .newText(invalidText)
                .build();

        // Act
        Set<ConstraintViolation<UpdateTextChatMessageRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("newText", violations.iterator().next().getPropertyPath().toString());
    }

}
