package com.example.bulletin.unit.application.data.response;

import com.example.bulletin.application.data.response.BulletinCharacteristicResponse;
import com.example.bulletin.application.data.response.BulletinImageResponse;
import com.example.bulletin.application.data.response.BulletinResponse;
import com.example.bulletin.application.data.response.CategoryResponse;
import com.example.bulletin.application.data.response.CharacteristicResponse;
import com.example.bulletin.application.data.response.CharacteristicValueResponse;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class BulletinResponseTests {

    private final UUID OWNER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID BULLETIN_ID = UUID.fromString("11111111-1111-1111-1111-111111111112");

    private final UUID PARENT_CATEGORY_ID = UUID.fromString("11111111-1111-1111-1111-111111111113");
    private final UUID CATEGORY_ID = UUID.fromString("11111111-1111-1111-1111-111111111114");

    private final UUID CHARACTERISTIC_ID = UUID.fromString("11111111-1111-1111-1111-111111111115");
    private final UUID CHARACTERISTIC_VALUE_ID = UUID.fromString("11111111-1111-1111-1111-111111111116");

    private final UUID IMAGE_ID = UUID.fromString("11111111-1111-1111-1111-111111111117");

    @Test
    void shouldReturnTrueWhenAllFieldsMatchExceptId() {
        // Arrange
        BulletinResponse data1 = createResponseBuilder()
                .build();

        BulletinResponse data2 = createResponseBuilder()
                .build();

        // Act
        boolean result = data1.equalsData(data2);

        // Assert
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenDifferentState() {
        // Arrange
        BulletinResponse data1 = createResponseBuilder()
                .build();

        BulletinResponse data2 = createResponseBuilder()
                .state(BulletinState.COMPLETED)
                .build();

        // Act
        boolean result = data1.equalsData(data2);

        // Assert
        assertFalse(result);
    }

    @Test
    void shouldReturnTrueWhenAllFieldsMatchExceptIdAndCategoryIsNull() {
        // Arrange
        BulletinResponse data1 = createResponseBuilder()
                .category(null)
                .build();

        BulletinResponse data2 = createResponseBuilder()
                .category(null)
                .build();

        // Act
        boolean result = data1.equalsData(data2);

        // Assert
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenCharacteristicsNotTheSame() {
        // Arrange
        BulletinResponse data1 = createResponseBuilder().build();

        BulletinCharacteristicResponse anotherBulletinCharacteristic = createBulletinCharacteristic(
                "another characteristic",
                "another value"
        );

        BulletinResponse data2 = createResponseBuilder()
                .characteristics(List.of(anotherBulletinCharacteristic))
                .build();

        // Act
        boolean result = data1.equalsData(data2);

        // Assert
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenImagesNotTheSame() {
        // Arrange
        BulletinResponse data1 = createResponseBuilder().build();

        UUID anotherImageId = UUID.fromString("11111111-1111-1111-1111-111111111118");
        BulletinImageResponse anotherImage = createBulletinImage(anotherImageId);

        BulletinResponse data2 = createResponseBuilder()
                .images(List.of(anotherImage))
                .build();

        // Act
        boolean result = data1.equalsData(data2);

        // Assert
        assertFalse(result);
    }

    private BulletinResponse.BulletinResponseBuilder createResponseBuilder() {
        CategoryResponse category = createCategory();
        BulletinCharacteristicResponse bulletinCharacteristicData = createBulletinCharacteristic(
                "characteristic 1",
                "value 1");
        BulletinImageResponse imageData = createBulletinImage(IMAGE_ID);

        return BulletinResponse.builder()
                .id(BULLETIN_ID)
                .ownerId(OWNER_ID)
                .title("title")
                .description("description")
                .price(1000)
                .rating(0)
                .state(BulletinState.CREATED)
                .category(category)
                .characteristics(List.of(bulletinCharacteristicData))
                .images(List.of(imageData));
    }

    private CategoryResponse createCategory() {
        return CategoryResponse.builder()
                .id(CATEGORY_ID)
                .name("category 1")
                .parentId(PARENT_CATEGORY_ID)
                .leaf(true)
                .build();
    }

    private BulletinCharacteristicResponse createBulletinCharacteristic(String characteristicName, String valueName) {
        CharacteristicResponse characteristic = createCharacteristic(characteristicName);
        CharacteristicValueResponse characteristicValue = createCharacteristicValue(valueName);

        return BulletinCharacteristicResponse.builder()
                .id(UUID.randomUUID())
                .bulletinId(BULLETIN_ID)
                .name(characteristic)
                .value(characteristicValue)
                .build();
    }

    private CharacteristicResponse createCharacteristic(String name) {
        return CharacteristicResponse.builder()
                .id(CHARACTERISTIC_ID)
                .name(name)
                .categoryId(CATEGORY_ID)
                .build();
    }

    private CharacteristicValueResponse createCharacteristicValue(String name) {
            return CharacteristicValueResponse.builder()
                    .id(CHARACTERISTIC_VALUE_ID)
                    .name(name)
                    .characteristicId(CHARACTERISTIC_ID)
                    .build();
    }

    private BulletinImageResponse createBulletinImage(UUID imageId) {
        return BulletinImageResponse.builder()
                .id(UUID.randomUUID())
                .bulletinId(BULLETIN_ID)
                .imageId(imageId)
                .main(true)
                .build();
    }

}
