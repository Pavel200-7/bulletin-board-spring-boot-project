package com.example.chat.unit.application.service.chatroom.data.request;

import com.example.chat.application.service.chatroom.data.request.GetFirstMessagePageRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetFirstMessagePageRequestTests {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassWhenGetFirstMessagePageRequestValid() {
        // given
        GetFirstMessagePageRequest request = GetFirstMessagePageRequest.builder()
                .chatId(UUID.randomUUID())
                .size(50)
                .build();

        // when
        Set<ConstraintViolation<GetFirstMessagePageRequest>> violations = validator.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenGetFirstMessagePageRequestChatIdIsNull() {
        // given
        GetFirstMessagePageRequest request = GetFirstMessagePageRequest.builder()
                .chatId(null)
                .size(50)
                .build();

        // when
        Set<ConstraintViolation<GetFirstMessagePageRequest>> violations = validator.validate(request);

        // then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("chatId", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailWhenGetFirstMessagePageRequestSizeIsLessThanMin() {
        // given
        GetFirstMessagePageRequest request = GetFirstMessagePageRequest.builder()
                .chatId(UUID.randomUUID())
                .size(-1)
                .build();

        // when
        Set<ConstraintViolation<GetFirstMessagePageRequest>> violations = validator.validate(request);

        // then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("size", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailWhenGetFirstMessagePageRequestSizeIsGreaterThanMax() {
        // given
        GetFirstMessagePageRequest request = GetFirstMessagePageRequest.builder()
                .chatId(UUID.randomUUID())
                .size(101)
                .build();

        // when
        Set<ConstraintViolation<GetFirstMessagePageRequest>> violations = validator.validate(request);

        // then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("size", violations.iterator().next().getPropertyPath().toString());
    }

}