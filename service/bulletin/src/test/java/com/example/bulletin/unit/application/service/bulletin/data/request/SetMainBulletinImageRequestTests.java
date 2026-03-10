package com.example.bulletin.unit.application.service.bulletin.data.request;

import com.example.bulletin.application.service.bulletin.data.request.SetMainBulletinImageRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class SetMainBulletinImageRequestTests {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldApproveWhenValid() {
        // Arrange
        SetMainBulletinImageRequest request = createValidRequestBuilder()
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    public void shouldForbidBlankBulletinId(UUID bulletinId) {
        // Arrange
        SetMainBulletinImageRequest request = createValidRequestBuilder()
                .bulletinId(bulletinId)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    public void shouldForbidBlankImageId(UUID imageId) {
        // Arrange
        SetMainBulletinImageRequest request = createValidRequestBuilder()
                .imageId(imageId)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    public SetMainBulletinImageRequest.SetMainBulletinImageRequestBuilder createValidRequestBuilder() {
        return SetMainBulletinImageRequest.builder()
                .bulletinId(UUID.randomUUID())
                .imageId(UUID.randomUUID());
    }

}
