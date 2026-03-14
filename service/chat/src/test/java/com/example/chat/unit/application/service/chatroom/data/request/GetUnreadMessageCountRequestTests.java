package com.example.chat.unit.application.service.chatroom.data.request;

import com.example.chat.application.service.chatroom.data.request.GetUnreadMessageCountRequest;
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
public class GetUnreadMessageCountRequestTests {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldApproveValidRequest() {
        // Arrange
        GetUnreadMessageCountRequest request = GetUnreadMessageCountRequest.builder()
                .chatId(UUID.randomUUID())
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    public void shouldForbidNullChatId(UUID chatId) {
        // Arrange
        GetUnreadMessageCountRequest request = GetUnreadMessageCountRequest.builder()
                .chatId(chatId)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

}