package com.example.bulletin.unit.application.service.characteristic.data.request;

import com.example.bulletin.application.service.characteristic.data.request.CreateCharacteristicRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CreateCharacteristicRequestTests {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldApproveWhenValid() {
        // Arrange
        CreateCharacteristicRequest request = createValidRequestBuilder()
                .build();
        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    public void shouldForbidBlankCategoryId(UUID categoryId) {
        // Arrange
        CreateCharacteristicRequest request = createValidRequestBuilder()
                .categoryId(categoryId)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", ""})
    public void shouldForbidBlankName(String invalidName) {
        // Arrange
        CreateCharacteristicRequest request = createValidRequestBuilder()
                .name(invalidName)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"*&&&*", "H*(&#Q#"})
    public void shouldForbidNameWithNotLettersAndDigits(String invalidName) {
        // Arrange
        CreateCharacteristicRequest request = createValidRequestBuilder()
                .name(invalidName)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    public CreateCharacteristicRequest.CreateCharacteristicRequestBuilder createValidRequestBuilder() {
        return CreateCharacteristicRequest.builder()
                .categoryId(UUID.randomUUID())
                .name("valid name");
    }
}
