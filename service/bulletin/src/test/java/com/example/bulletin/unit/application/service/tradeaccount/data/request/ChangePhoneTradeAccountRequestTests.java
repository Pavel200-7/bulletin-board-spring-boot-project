package com.example.bulletin.unit.application.service.tradeaccount.data.request;

import com.example.bulletin.application.service.tradeaccount.data.request.ChangePhoneTradeAccountRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class ChangePhoneTradeAccountRequestTests {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldApproveWhenValid() {
        // Arrange
        ChangePhoneTradeAccountRequest request = createValidRequestBuilder()
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "  "})
    public void shouldForbidBlankPhone(String phone) {
        // Arrange
        ChangePhoneTradeAccountRequest request = createValidRequestBuilder()
                .phone(phone)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123456789",        // слишком короткий (9 цифр)
            "1234567890123456", // слишком длинный (16 цифр)
            "phone12345",       // содержит буквы
            "8-999-123-45-67",  // содержит дефисы
            "8 999 123 45 67",  // содержит пробелы
            "(999)1234567",     // содержит скобки
            "8.999.123.45.67"   // содержит точки
    })
    public void shouldForbidInvalidPhoneFormats(String phone) {
        // Arrange
        ChangePhoneTradeAccountRequest request = createValidRequestBuilder()
                .phone(phone)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "+79991234567",     // с плюсом
            "79991234567",      // без плюса
            "89991234567",      // российский формат
            "1234567890",       // 10 цифр
            "123456789012345",  // 15 цифр
            "+123456789012345"  // 15 цифр с плюсом
    })
    public void shouldApproveValidPhoneFormats(String phone) {
        // Arrange
        ChangePhoneTradeAccountRequest request = createValidRequestBuilder()
                .phone(phone)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldForbidWhenLengthLessThanMin() {
        // Arrange
        ChangePhoneTradeAccountRequest request = createValidRequestBuilder()
                .phone("123456789")
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldForbidWhenLengthMoreThanMax() {
        // Arrange
        ChangePhoneTradeAccountRequest request = createValidRequestBuilder()
                .phone("1234567890123456")
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    public ChangePhoneTradeAccountRequest.ChangePhoneTradeAccountRequestBuilder createValidRequestBuilder() {
        return ChangePhoneTradeAccountRequest.builder()
                .phone("+79991234567");
    }
}
