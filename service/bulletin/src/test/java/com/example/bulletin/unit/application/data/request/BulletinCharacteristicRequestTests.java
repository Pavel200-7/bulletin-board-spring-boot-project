package com.example.bulletin.unit.application.data.request;

import com.example.bulletin.application.data.request.BulletinCharacteristicRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class BulletinCharacteristicRequestTests {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldApproveWhenValid() {
        // Arrange
        BulletinCharacteristicRequest request = createValidRequestBuilder()
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    public void shouldForbidNullCharacteristicId(UUID characteristicId) {
        // Arrange
        BulletinCharacteristicRequest request = createValidRequestBuilder()
                .characteristicId(characteristicId)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Characteristic ID must not be null", violations.iterator().next().getMessage());
    }

    @ParameterizedTest
    @NullSource
    public void shouldForbidNullCharacteristicValueId(UUID characteristicValueId) {
        // Arrange
        BulletinCharacteristicRequest request = createValidRequestBuilder()
                .characteristicValueId(characteristicValueId)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Characteristic value ID must not be null", violations.iterator().next().getMessage());
    }

    @Test
    public void shouldCollectMultipleViolations() {
        // Arrange
        BulletinCharacteristicRequest request = createValidRequestBuilder()
                .characteristicId(null)
                .characteristicValueId(null)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());
    }

    public BulletinCharacteristicRequest.BulletinCharacteristicRequestBuilder createValidRequestBuilder() {
        return BulletinCharacteristicRequest.builder()
                .characteristicId(UUID.randomUUID())
                .characteristicValueId(UUID.randomUUID());
    }

}