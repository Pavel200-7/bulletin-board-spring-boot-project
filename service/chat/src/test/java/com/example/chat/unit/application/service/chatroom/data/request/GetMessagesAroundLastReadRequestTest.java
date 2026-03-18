package com.example.chat.unit.application.service.chatroom.data.request;

import com.example.chat.application.service.chatroom.data.request.GetMessagesAroundLastReadRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetMessagesAroundLastReadRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }



    @Test
    void shouldPassWhenGetMessagesAroundLastReadRequestValid() {
        // given
        GetMessagesAroundLastReadRequest request = GetMessagesAroundLastReadRequest.builder()
                .chatId(UUID.randomUUID())
                .size(50)
                .build();

        // when
        Set<ConstraintViolation<GetMessagesAroundLastReadRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenGetMessagesAroundLastReadRequestChatIdIsNull() {
        // given
        GetMessagesAroundLastReadRequest request = GetMessagesAroundLastReadRequest.builder()
                .chatId(null)
                .size(50)
                .build();

        // when
        Set<ConstraintViolation<GetMessagesAroundLastReadRequest>> violations = validator.validate(request);

        // then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("chatId", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailWhenGetMessagesAroundLastReadRequestSizeIsLessThanMin() {
        // given
        GetMessagesAroundLastReadRequest request = GetMessagesAroundLastReadRequest.builder()
                .chatId(UUID.randomUUID())
                .size(-1)
                .build();

        // when
        Set<ConstraintViolation<GetMessagesAroundLastReadRequest>> violations = validator.validate(request);

        // then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("size", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailWhenGetMessagesAroundLastReadRequestSizeIsGreaterThanMax() {
        // given
        GetMessagesAroundLastReadRequest request = GetMessagesAroundLastReadRequest.builder()
                .chatId(UUID.randomUUID())
                .size(101)
                .build();

        // when
        Set<ConstraintViolation<GetMessagesAroundLastReadRequest>> violations = validator.validate(request);

        // then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("size", violations.iterator().next().getPropertyPath().toString());
    }

}