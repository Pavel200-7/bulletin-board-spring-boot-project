package com.example.bulletin.unit.application.service.characteristicvalue.data.request;

import com.example.bulletin.application.service.characteristic.data.request.RenameCharacteristicRequest;
import com.example.bulletin.application.service.characteristic_value.data.request.RenameCharacteristicValueRequest;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RenameCharacteristicValueRequestTests {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldApproveWhenValid() {
        // Arrange
        RenameCharacteristicValueRequest request = createValidRequestBuilder()
                .build();
        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    public void shouldForbidBlankCategoryId(UUID parentId) {
        // Arrange
        RenameCharacteristicValueRequest request = createValidRequestBuilder()
                .id(parentId)
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
        RenameCharacteristicValueRequest request = createValidRequestBuilder()
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
        RenameCharacteristicValueRequest request = createValidRequestBuilder()
                .name(invalidName)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    public RenameCharacteristicValueRequest.RenameCharacteristicValueRequestBuilder createValidRequestBuilder() {
        return RenameCharacteristicValueRequest.builder()
                .id(UUID.randomUUID())
                .name("valid name");
    }
}
