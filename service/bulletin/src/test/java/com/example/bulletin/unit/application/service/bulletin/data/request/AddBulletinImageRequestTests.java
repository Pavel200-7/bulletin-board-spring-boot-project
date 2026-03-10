package com.example.bulletin.unit.application.service.bulletin.data.request;

import com.example.bulletin.application.service.bulletin.data.request.AddBulletinImageRequest;
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
public class AddBulletinImageRequestTests {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldApproveWhenValid() {
        // Arrange
        AddBulletinImageRequest request = createValidRequestBuilder()
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
        AddBulletinImageRequest request = createValidRequestBuilder()
                .bulletinId(bulletinId)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    public void shouldForbidBlankProviderImageId(UUID providerImageId) {
        // Arrange
        AddBulletinImageRequest request = createValidRequestBuilder()
                .providerImageId(providerImageId)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    public AddBulletinImageRequest.AddBulletinImageRequestBuilder createValidRequestBuilder() {
        return AddBulletinImageRequest.builder()
                .bulletinId(UUID.randomUUID())
                .providerImageId(UUID.randomUUID());
    }

}
