package com.example.chat.unit.application.service.profile.request;

import com.example.chat.application.service.profile.data.request.ChangePublicNameRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class ChangePublicNameRequestTests {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldApproveWhenValid() {
        // Arrange
        ChangePublicNameRequest request = ChangePublicNameRequest.builder()
                .name("New Public Name")
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    public void shouldForbidBlankName(String invalidName) {
        // Arrange
        ChangePublicNameRequest request = ChangePublicNameRequest.builder()
                .name(invalidName)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"*&&&*", "H*(&#Q#", "test@name", "name-with-dashes"})
    public void shouldForbidNameWithSpecialCharacters(String invalidName) {
        // Arrange
        ChangePublicNameRequest request = ChangePublicNameRequest.builder()
                .name(invalidName)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldAllowNameWithRussianLetters() {
        // Arrange
        ChangePublicNameRequest request = ChangePublicNameRequest.builder()
                .name("Иван Петров")
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldAllowNameWithEnglishLetters() {
        // Arrange
        ChangePublicNameRequest request = ChangePublicNameRequest.builder()
                .name("John Doe")
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldAllowNameWithDigits() {
        // Arrange
        ChangePublicNameRequest request = ChangePublicNameRequest.builder()
                .name("User123")
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

}
