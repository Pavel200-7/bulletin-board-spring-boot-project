package com.example.bulletin.unit.application.service.tradeaccount.data.request;

import com.example.bulletin.application.service.tradeaccount.data.request.RenameTradeAccountRequest;
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
public class RenameTradeAccountRequestTests {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldApproveWhenValid() {
        // Arrange
        RenameTradeAccountRequest request = createValidRequestBuilder()
                .build();
        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    public void shouldForbidBlankId(UUID id) {
        // Arrange
        RenameTradeAccountRequest request = createValidRequestBuilder()
                .id(id)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "  "})
    public void shouldForbidBlankName(String name) {
        // Arrange
        RenameTradeAccountRequest request = createValidRequestBuilder()
                .name(name)
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
        RenameTradeAccountRequest request = createValidRequestBuilder()
                .name(invalidName)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"gd", "va", "af"})
    public void shouldForbidSmallName(String name) {
        // Arrange
        RenameTradeAccountRequest request = createValidRequestBuilder()
                .name(name)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = "gdgdgdgdgdgdgdgdgdgdgdgdgdgdgdgdgdgdgdgdgdgdgdgdgdgdgdgdgdgdgdgdgdgdg71")
    public void shouldForbidLongName(String name) {
        // Arrange
        RenameTradeAccountRequest request = createValidRequestBuilder()
                .name(name)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    public RenameTradeAccountRequest.RenameTradeAccountRequestBuilder createValidRequestBuilder() {
        return RenameTradeAccountRequest.builder()
                .id(UUID.randomUUID())
                .name("new name");
    }

}
