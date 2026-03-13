package com.example.chat.unit.application.service.profile.request;

import com.example.chat.application.service.profile.data.request.ChangeDescriptionRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class ChangeDescriptionRequestTests {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldApproveWhenValid() {
        // Arrange
        ChangeDescriptionRequest request = ChangeDescriptionRequest.builder()
                .description("This is a valid description")
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldForbidDescriptionExceedingMaxLength() {
        // Arrange
        String tooLongDescription = "a".repeat(501);
        ChangeDescriptionRequest request = ChangeDescriptionRequest.builder()
                .description(tooLongDescription)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldAllowDescriptionWithSpecialCharacters() {
        // Arrange
        ChangeDescriptionRequest request = ChangeDescriptionRequest.builder()
                .description("Description with special chars: !@#$%^&*()")
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

}