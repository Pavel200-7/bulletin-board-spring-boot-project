package com.example.bulletin.unit.application.data.request;

import com.example.bulletin.application.data.request.BulletinCharacteristicRequest;
import com.example.bulletin.application.data.request.BulletinRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class BulletinRequestTests {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldApproveWhenValid() {
        // Arrange
        BulletinRequest request = createValidRequestBuilder()
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldApproveWhenCharacteristicsIsNull() {
        // Arrange
        BulletinRequest request = createValidRequestBuilder()
                .characteristics(null)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldApproveWhenCharacteristicsIsEmpty() {
        // Arrange
        BulletinRequest request = createValidRequestBuilder()
                .characteristics(List.of())
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
        BulletinRequest request = createValidRequestBuilder()
                .id(id)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("ID must not be null", violations.iterator().next().getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", ""})
    public void shouldForbidBlankTitle(String invalidTitle) {
        // Arrange
        BulletinRequest request = createValidRequestBuilder()
                .title(invalidTitle)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Title must not be blank", violations.iterator().next().getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", ""})
    public void shouldForbidBlankDescription(String invalidDescription) {
        // Arrange
        BulletinRequest request = createValidRequestBuilder()
                .description(invalidDescription)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Description must not be blank", violations.iterator().next().getMessage());
    }

    @ParameterizedTest
    @NullSource
    public void shouldForbidNullPrice(Double price) {
        // Arrange
        BulletinRequest request = createValidRequestBuilder()
                .price(price)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Price must not be null", violations.iterator().next().getMessage());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-10.0, -1.0, 0.0})
    public void shouldForbidNonPositivePrice(double invalidPrice) {
        // Arrange
        BulletinRequest request = createValidRequestBuilder()
                .price(invalidPrice)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Price must be positive", violations.iterator().next().getMessage());
    }

    @ParameterizedTest
    @NullSource
    public void shouldForbidNullCategoryId(UUID categoryId) {
        // Arrange
        BulletinRequest request = createValidRequestBuilder()
                .categoryId(categoryId)
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Category ID must not be null", violations.iterator().next().getMessage());
    }

    @Test
    public void shouldValidateNestedCharacteristics() {
        // Arrange
        BulletinCharacteristicRequest invalidCharacteristic = BulletinCharacteristicRequest.builder()
                .characteristicId(null)  // невалидно
                .characteristicValueId(UUID.randomUUID())
                .build();

        BulletinRequest request = createValidRequestBuilder()
                .characteristics(List.of(invalidCharacteristic))
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());

        // Должна быть ошибка во вложенном объекте
        assertEquals(1, violations.size());
        assertEquals("Characteristic ID must not be null", violations.iterator().next().getMessage());
    }

    @Test
    public void shouldCollectMultipleViolationsFromNestedCharacteristics() {
        // Arrange
        BulletinCharacteristicRequest invalidCharacteristic1 = BulletinCharacteristicRequest.builder()
                .characteristicId(null)
                .characteristicValueId(null)
                .build();

        BulletinCharacteristicRequest invalidCharacteristic2 = BulletinCharacteristicRequest.builder()
                .characteristicId(UUID.randomUUID())
                .characteristicValueId(null)
                .build();

        BulletinRequest request = createValidRequestBuilder()
                .characteristics(List.of(invalidCharacteristic1, invalidCharacteristic2))
                .build();

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(3, violations.size());  // 2 ошибки в первом + 1 ошибка во втором
    }

    public BulletinRequest.BulletinRequestBuilder createValidRequestBuilder() {
        return BulletinRequest.builder()
                .id(UUID.randomUUID())
                .title("Valid Title")
                .description("Valid description that is long enough")
                .price(100.0)
                .categoryId(UUID.randomUUID())
                .characteristics(List.of(
                        BulletinCharacteristicRequest.builder()
                                .characteristicId(UUID.randomUUID())
                                .characteristicValueId(UUID.randomUUID())
                                .build()
                ));
    }

}