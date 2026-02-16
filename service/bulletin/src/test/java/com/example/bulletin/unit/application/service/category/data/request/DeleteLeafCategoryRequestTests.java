package com.example.bulletin.unit.application.service.category.data.request;

import com.example.bulletin.application.service.category.data.request.DeleteLeafCategoryRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeleteLeafCategoryRequestTests {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldApproveWhenValid() {
        // Arrange
        DeleteLeafCategoryRequest request = createValidRequestBuilder()
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
        DeleteLeafCategoryRequest request = createValidRequestBuilder()
                .id(id)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    public DeleteLeafCategoryRequest.DeleteLeafCategoryRequestBuilder createValidRequestBuilder() {
        return DeleteLeafCategoryRequest.builder()
                .id(UUID.randomUUID());
    }
}
