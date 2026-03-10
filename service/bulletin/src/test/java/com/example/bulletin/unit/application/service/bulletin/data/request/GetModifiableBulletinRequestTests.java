package com.example.bulletin.unit.application.service.bulletin.data.request;

import com.example.bulletin.application.service.bulletin.data.request.GetBulletinRequest;
import com.example.bulletin.application.service.bulletin.data.request.GetModifiableBulletinRequest;
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
public class GetModifiableBulletinRequestTests {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldApproveWhenValid() {
        // Arrange
        GetModifiableBulletinRequest request = createValidRequestBuilder()
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
        GetModifiableBulletinRequest request = createValidRequestBuilder()
                .bulletinId(bulletinId)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    public GetModifiableBulletinRequest.GetModifiableBulletinRequestBuilder createValidRequestBuilder() {
        return GetModifiableBulletinRequest.builder()
                .bulletinId(UUID.randomUUID());
    }
}
