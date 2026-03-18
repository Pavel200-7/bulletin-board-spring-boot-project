package com.example.chat.unit.application.service.chatroom.data.request;

import com.example.chat.application.service.chatroom.data.request.GetMessagePaginationRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort.Direction;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetMessagePaginationRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassWhenAllFieldsValid() {
        // given
        GetMessagePaginationRequest request = GetMessagePaginationRequest.builder()
                .chatId(UUID.randomUUID())
                .cursorMessageId(UUID.randomUUID())
                .direction(Direction.ASC)
                .size(50)
                .build();

        // when
        Set<ConstraintViolation<GetMessagePaginationRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenChatIdIsNull() {
        // given
        GetMessagePaginationRequest request = GetMessagePaginationRequest.builder()
                .chatId(null)
                .cursorMessageId(UUID.randomUUID())
                .direction(Direction.ASC)
                .size(50)
                .build();

        // when
        Set<ConstraintViolation<GetMessagePaginationRequest>> violations = validator.validate(request);

        // then
        assertEquals(1, violations.size());
        ConstraintViolation<GetMessagePaginationRequest> violation = violations.iterator().next();
        assertEquals("chatId", violation.getPropertyPath().toString());
    }

    @Test
    void shouldFailWhenCursorMessageIdIsNull() {
        // given
        GetMessagePaginationRequest request = GetMessagePaginationRequest.builder()
                .chatId(UUID.randomUUID())
                .cursorMessageId(null)
                .direction(Direction.ASC)
                .size(50)
                .build();

        // when
        Set<ConstraintViolation<GetMessagePaginationRequest>> violations = validator.validate(request);

        // then
        assertEquals(1, violations.size());
        assertEquals("cursorMessageId", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailWhenDirectionIsNull() {
        // given
        GetMessagePaginationRequest request = GetMessagePaginationRequest.builder()
                .chatId(UUID.randomUUID())
                .cursorMessageId(UUID.randomUUID())
                .direction(null)
                .size(50)
                .build();

        // when
        Set<ConstraintViolation<GetMessagePaginationRequest>> violations = validator.validate(request);

        // then
        assertEquals(1, violations.size());
        assertEquals("direction", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailWhenSizeIsLessThanMin() {
        // given
        GetMessagePaginationRequest request = GetMessagePaginationRequest.builder()
                .chatId(UUID.randomUUID())
                .cursorMessageId(UUID.randomUUID())
                .direction(Direction.ASC)
                .size(-1)
                .build();

        // when
        Set<ConstraintViolation<GetMessagePaginationRequest>> violations = validator.validate(request);

        // then
        assertEquals(1, violations.size());
        ConstraintViolation<GetMessagePaginationRequest> violation = violations.iterator().next();
        assertEquals("size", violation.getPropertyPath().toString());
    }

    @Test
    void shouldFailWhenSizeIsGreaterThanMax() {
        // given
        GetMessagePaginationRequest request = GetMessagePaginationRequest.builder()
                .chatId(UUID.randomUUID())
                .cursorMessageId(UUID.randomUUID())
                .direction(Direction.ASC)
                .size(101)
                .build();

        // when
        Set<ConstraintViolation<GetMessagePaginationRequest>> violations = validator.validate(request);

        // then
        assertEquals(1, violations.size());
        assertEquals("size", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldPassWhenSizeIsAtMinBoundary() {
        // given
        GetMessagePaginationRequest request = GetMessagePaginationRequest.builder()
                .chatId(UUID.randomUUID())
                .cursorMessageId(UUID.randomUUID())
                .direction(Direction.ASC)
                .size(0)
                .build();

        // when
        Set<ConstraintViolation<GetMessagePaginationRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldPassWhenSizeIsAtMaxBoundary() {
        // given
        GetMessagePaginationRequest request = GetMessagePaginationRequest.builder()
                .chatId(UUID.randomUUID())
                .cursorMessageId(UUID.randomUUID())
                .direction(Direction.ASC)
                .size(100)
                .build();

        // when
        Set<ConstraintViolation<GetMessagePaginationRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldCollectMultipleViolations() {
        // given
        GetMessagePaginationRequest request = GetMessagePaginationRequest.builder()
                .chatId(null)
                .cursorMessageId(UUID.randomUUID())
                .direction(null)
                .size(-5)
                .build();

        // when
        Set<ConstraintViolation<GetMessagePaginationRequest>> violations = validator.validate(request);

        // then
        assertEquals(3, violations.size());
    }
}