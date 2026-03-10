package com.example.bulletin.unit.application.service.tradeaccount.data.request;

import com.example.bulletin.application.service.tradeaccount.data.request.SetApproximateLocationTradeAccountRequest;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class SetApproximateLocationSetExactLocationRequestTests {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldApproveWhenValid() {
        // Arrange
        SetApproximateLocationTradeAccountRequest request = createValidRequestBuilder()
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    public void shouldForbidNullLatitude(Double latitude) {
        // Arrange
        SetApproximateLocationTradeAccountRequest request = createValidRequestBuilder()
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
        SetApproximateLocationTradeAccountRequest request = createValidRequestBuilder()
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
        SetApproximateLocationTradeAccountRequest request = createValidRequestBuilder()
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
        SetApproximateLocationTradeAccountRequest request = createValidRequestBuilder()
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
        SetApproximateLocationTradeAccountRequest request = createValidRequestBuilder()
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
        SetApproximateLocationTradeAccountRequest request = createValidRequestBuilder()
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
        SetApproximateLocationTradeAccountRequest request = createValidRequestBuilder()
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
        SetApproximateLocationTradeAccountRequest request = createValidRequestBuilder()
                .townName(townName)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    public SetApproximateLocationTradeAccountRequest.SetApproximateLocationTradeAccountRequestBuilder createValidRequestBuilder() {
        return SetApproximateLocationTradeAccountRequest.builder()
                .latitude(55.7558)
                .longitude(37.6173)
                .townName("Moscow");
    }
}