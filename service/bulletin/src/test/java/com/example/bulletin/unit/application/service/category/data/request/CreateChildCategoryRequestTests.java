package com.example.bulletin.unit.application.service.category.data.request;

import com.example.bulletin.application.service.category.data.request.CreateChildCategoryRequest;
import com.example.bulletin.application.service.category.data.request.CreateRootCategoryRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CreateChildCategoryRequestTests {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldApproveWhenValid() {
        // Arrange
        CreateChildCategoryRequest request = createValidRequestBuilder()
                .build();
        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    public void shouldForbidBlankParentId(UUID parentId) {
        // Arrange
        CreateChildCategoryRequest request = createValidRequestBuilder()
                .parentId(parentId)
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
        CreateChildCategoryRequest request = createValidRequestBuilder()
                .name(invalidName)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    public CreateChildCategoryRequest.CreateChildCategoryRequestBuilder createValidRequestBuilder() {
        return CreateChildCategoryRequest.builder()
                .parentId(UUID.randomUUID())
                .name("valid name");
    }
}
