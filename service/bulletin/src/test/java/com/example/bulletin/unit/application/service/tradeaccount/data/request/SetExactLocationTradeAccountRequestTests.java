package com.example.bulletin.unit.application.service.tradeaccount.data.request;

import com.example.bulletin.application.service.tradeaccount.data.request.SetExactLocationTradeAccountRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SetExactLocationTradeAccountRequestTests {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldApproveWhenValid() {
        // Arrange
        SetExactLocationTradeAccountRequest request = createValidRequestBuilder()
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    public void shouldForbidNullId(UUID id) {
        // Arrange
        SetExactLocationTradeAccountRequest request = createValidRequestBuilder()
                .id(id)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    public void shouldForbidNullLatitude(Double latitude) {
        // Arrange
        SetExactLocationTradeAccountRequest request = createValidRequestBuilder()
                .latitude(latitude)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    public void shouldForbidNullLongitude(Double longitude) {
        // Arrange
        SetExactLocationTradeAccountRequest request = createValidRequestBuilder()
                .longitude(longitude)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-90.1, -100.0, 90.1, 100.0})
    public void shouldForbidInvalidLatitude(double latitude) {
        // Arrange
        SetExactLocationTradeAccountRequest request = createValidRequestBuilder()
                .latitude(latitude)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-90.0, -45.5, 0.0, 45.5, 90.0})
    public void shouldApproveValidLatitude(double latitude) {
        // Arrange
        SetExactLocationTradeAccountRequest request = createValidRequestBuilder()
                .latitude(latitude)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-180.1, -200.0, 180.1, 200.0})
    public void shouldForbidInvalidLongitude(double longitude) {
        // Arrange
        SetExactLocationTradeAccountRequest request = createValidRequestBuilder()
                .longitude(longitude)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-180.0, -90.5, 0.0, 90.5, 180.0})
    public void shouldApproveValidLongitude(double longitude) {
        // Arrange
        SetExactLocationTradeAccountRequest request = createValidRequestBuilder()
                .longitude(longitude)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldForbidTownNameLongerThan100() {
        // Arrange
        String townName = "a".repeat(101);
        SetExactLocationTradeAccountRequest request = createValidRequestBuilder()
                .townName(townName)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "  "})
    public void shouldForbidNullTownName(String townName) {
        // Arrange
        SetExactLocationTradeAccountRequest request = createValidRequestBuilder()
                .townName(townName)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldForbidLocationNameLongerThan300() {
        // Arrange
        String locationName = "a".repeat(301);
        SetExactLocationTradeAccountRequest request = createValidRequestBuilder()
                .locationName(locationName)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "  "})
    public void shouldForbidNullLocationName(String locationName) {
        // Arrange
        SetExactLocationTradeAccountRequest request = createValidRequestBuilder()
                .locationName(locationName)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    public SetExactLocationTradeAccountRequest.SetExactLocationTradeAccountRequestBuilder createValidRequestBuilder() {
        return SetExactLocationTradeAccountRequest.builder()
                .id(UUID.randomUUID())
                .latitude(55.7558)
                .longitude(37.6173)
                .townName("Moscow")
                .locationName("Moscow, some street, some... ");
    }
}
