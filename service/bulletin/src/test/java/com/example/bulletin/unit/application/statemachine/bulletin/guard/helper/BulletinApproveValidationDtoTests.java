package com.example.bulletin.unit.application.statemachine.bulletin.guard.helper;

import com.example.bulletin.application.statemachine.bulletin.guard.helper.BulletinApproveValidationDto;
import com.example.bulletin.application.statemachine.bulletin.guard.helper.CharacteristicValidationDto;
import com.example.bulletin.application.statemachine.bulletin.guard.helper.ImageValidationDto;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class BulletinApproveValidationDtoTests {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldApproveWhenValid() {
        // Arrange
        BulletinApproveValidationDto dto = createValidDto();

        // Act
        var violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "  "})
    public void shouldForbidBlankTitle(String title) {
        // Arrange
        BulletinApproveValidationDto dto = createValidDto();
        dto.setTitle(title);

        // Act
        var violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("title") &&
                        v.getMessage().equals("Title is required")));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "  "})
    public void shouldForbidBlankDescription(String description) {
        // Arrange
        BulletinApproveValidationDto dto = createValidDto();
        dto.setDescription(description);

        // Act
        var violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("description") &&
                        v.getMessage().equals("Description is required")));
    }

    @Test
    public void shouldForbidNullCategory() {
        // Arrange
        BulletinApproveValidationDto dto = createValidDto();
        dto.setCategoryId(null);

        // Act
        var violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("categoryId") &&
                        v.getMessage().equals("Category is required")));
    }

    @Test
    public void shouldForbidEmptyCharacteristics() {
        // Arrange
        BulletinApproveValidationDto dto = createValidDto();
        dto.setCharacteristics(List.of());

        // Act
        var violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("characteristics") &&
                        v.getMessage().equals("At least one characteristic is required")));
    }

    @Test
    public void shouldForbidNullCharacteristics() {
        // Arrange
        BulletinApproveValidationDto dto = createValidDto();
        dto.setCharacteristics(null);

        // Act
        var violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("characteristics") &&
                        v.getMessage().equals("At least one characteristic is required")));
    }

    @Test
    public void shouldForbidEmptyImages() {
        // Arrange
        BulletinApproveValidationDto dto = createValidDto();
        dto.setImages(List.of());

        // Act
        var violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("images") &&
                        v.getMessage().equals("At least one image is required")));
    }

    @Test
    public void shouldForbidNullImages() {
        // Arrange
        BulletinApproveValidationDto dto = createValidDto();
        dto.setImages(null);

        // Act
        var violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("images") &&
                        v.getMessage().equals("At least one image is required")));
    }

    @Test
    public void shouldForbidMoreThan10Images() {
        // Arrange
        BulletinApproveValidationDto dto = createValidDto();
        List<ImageValidationDto> tooManyImages = List.of(
                createValidImage(), createValidImage(), createValidImage(),
                createValidImage(), createValidImage(), createValidImage(),
                createValidImage(), createValidImage(), createValidImage(),
                createValidImage(), createValidImage() // 11 images
        );
        dto.setImages(tooManyImages);

        // Act
        var violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("images") &&
                        v.getMessage().equals("Cannot have more than 10 images")));
    }

    @Test
    public void shouldCollectMultipleViolations() {
        // Arrange
        BulletinApproveValidationDto dto = new BulletinApproveValidationDto();
        // Все поля null или пустые

        // Act
        var violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 5); // title, description, categoryId, characteristics, images
    }

    private BulletinApproveValidationDto createValidDto() {
        BulletinApproveValidationDto dto = new BulletinApproveValidationDto();
        dto.setTitle("Valid Bulletin Title");
        dto.setDescription("This is a valid description that is long enough to pass validation");
        dto.setCategoryId(UUID.randomUUID());
        dto.setCharacteristics(List.of(createValidCharacteristic()));
        dto.setImages(List.of(createValidImage()));
        return dto;
    }

    private CharacteristicValidationDto createValidCharacteristic() {
        return new CharacteristicValidationDto(
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }

    private ImageValidationDto createValidImage() {
        return new ImageValidationDto(
                UUID.randomUUID(),
                true
        );
    }

}