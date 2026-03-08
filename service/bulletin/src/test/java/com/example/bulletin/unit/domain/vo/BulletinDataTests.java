package com.example.bulletin.unit.domain.vo;

import com.example.bulletin.domain.enums.bulletin.BulletinState;
import com.example.bulletin.domain.vo.*;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class BulletinDataTests {

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
        BulletinData data1 = createDataBuilder()
                .build();

        BulletinData data2 = createDataBuilder()
                .build();

        // Act
        boolean result = data1.equalsData(data2);

        // Assert
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenDifferentState() {
        // Arrange
        BulletinData data1 = createDataBuilder()
                .build();

        BulletinData data2 = createDataBuilder()
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
        BulletinData data1 = createDataBuilder()
                .category(null)
                .build();

        BulletinData data2 = createDataBuilder()
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
        BulletinData data1 = createDataBuilder().build();

        BulletinCharacteristicData anotherBulletinCharacteristic = createBulletinCharacteristicData(
                "another characteristic",
                "another value"
        );

        BulletinData data2 = createDataBuilder()
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
        BulletinData data1 = createDataBuilder().build();

        UUID anotherImageId = UUID.fromString("11111111-1111-1111-1111-111111111118");
        BulletinImageData anotherImage = createBulletinImage(anotherImageId);

        BulletinData data2 = createDataBuilder()
                .images(List.of(anotherImage))
                .build();

        // Act
        boolean result = data1.equalsData(data2);

        // Assert
        assertFalse(result);
    }

    private BulletinData.BulletinDataBuilder createDataBuilder() {
        CategoryData categoryData = createCategoryData();
        BulletinCharacteristicData bulletinCharacteristicData = createBulletinCharacteristicData(
                "characteristic 1",
                "value 1");
        BulletinImageData imageData = createBulletinImage(IMAGE_ID);

        return BulletinData.builder()
                .id(BULLETIN_ID)
                .ownerId(OWNER_ID)
                .title("title")
                .description("description")
                .price(1000)
                .rating(0)
                .state(BulletinState.CREATED)
                .category(categoryData)
                .characteristics(List.of(bulletinCharacteristicData))
                .images(List.of(imageData));
    }

    private CategoryData createCategoryData() {
        return CategoryData.builder()
                .id(CATEGORY_ID)
                .name("category 1")
                .parentId(PARENT_CATEGORY_ID)
                .leaf(true)
                .build();
    }

    private BulletinCharacteristicData createBulletinCharacteristicData(String characteristicName, String valueName) {
        CharacteristicData characteristic = getCharacteristic(characteristicName);
        CharacteristicValueData characteristicValue = getCharacteristicValue(valueName);

        return BulletinCharacteristicData.builder()
                .id(UUID.randomUUID())
                .bulletinId(BULLETIN_ID)
                .name(characteristic)
                .value(characteristicValue)
                .build();
    }

    private CharacteristicData getCharacteristic(String name) {
        return CharacteristicData.builder()
                .id(CHARACTERISTIC_ID)
                .name(name)
                .categoryId(CATEGORY_ID)
                .build();
    }

    private CharacteristicValueData getCharacteristicValue(String name) {
            return CharacteristicValueData.builder()
                    .id(CHARACTERISTIC_VALUE_ID)
                    .name(name)
                    .characteristicId(CHARACTERISTIC_ID)
                    .build();
    }

    private BulletinImageData createBulletinImage(UUID imageId) {
        return BulletinImageData.builder()
                .id(UUID.randomUUID())
                .bulletinId(BULLETIN_ID)
                .imageId(imageId)
                .build();
    }

}
