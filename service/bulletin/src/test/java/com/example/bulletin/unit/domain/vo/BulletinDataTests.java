package com.example.bulletin.unit.domain.vo;

import com.example.bulletin.domain.enums.BulletinStatus;
import com.example.bulletin.domain.vo.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BulletinDataTests {

    BulletinData.BulletinDataBuilder bulletinDataBuilder = null;

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

        BulletinCharacteristicData originalCharacteristic = data1.getCharacteristics().get(0);

        CharacteristicData modifiedCharacteristicData = CharacteristicData.builder()
                .id(originalCharacteristic.getName().getId())
                .categoryId(originalCharacteristic.getName().getCategoryId())
                .name("another name")
                .build();

        BulletinCharacteristicData modifiedBulletinCharacteristic = BulletinCharacteristicData.builder()
                .id(originalCharacteristic.getId())
                .bulletinId(originalCharacteristic.getBulletinId())
                .name(modifiedCharacteristicData)
                .value(originalCharacteristic.getValue())
                .build();

        List<BulletinCharacteristicData> modifiedCharacteristics = List.of(modifiedBulletinCharacteristic);

        BulletinData data2 = createDataBuilder()
                .characteristics(modifiedCharacteristics)
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

        BulletinImageData originalImage = data1.getImages().get(0);

        BulletinImageData modifiedImage = BulletinImageData.builder()
                .id(originalImage.getId())
                .bulletinId(originalImage.getBulletinId())
                .imageId(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                .build();

        List<BulletinImageData> modifiedImages = List.of(modifiedImage);

        BulletinData data2 = createDataBuilder()
                .images(modifiedImages)
                .build();

        // Act
        boolean result = data1.equalsData(data2);

        // Assert
        assertFalse(result);
    }

    private BulletinData.BulletinDataBuilder createDataBuilder() {
        if (this.bulletinDataBuilder != null) {
            return this.bulletinDataBuilder;
        }

        UUID bulletinId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID ownerId = UUID.fromString("11111111-1111-1111-1111-111111111112");

        CategoryData categoryData = createCategoryData();
        UUID categoryId = categoryData.getId();

        List<BulletinCharacteristicData> bulletinCharacteristicsData = new ArrayList<>();
        BulletinCharacteristicData bulletinCharacteristicData = createBulletinCharacteristicData(bulletinId, categoryId);
        bulletinCharacteristicsData.add(bulletinCharacteristicData);

        List<BulletinImageData> bulletinImagesData = new ArrayList<>();
        BulletinImageData imageData = createBulletinImage(bulletinId);
        bulletinImagesData.add(imageData);

        return BulletinData.builder()
                .id(bulletinId)
                .ownerId(ownerId)
                .title("title")
                .description("description")
                .price(1000)
                .rating(0)
                .status(BulletinStatus.DRAFT)
                .category(categoryData)
                .characteristics(bulletinCharacteristicsData)
                .images(bulletinImagesData);
    }

    private CategoryData createCategoryData() {
        return CategoryData.builder()
                .id(UUID.fromString("11111111-1111-1111-1111-111111111113"))
                .name("category 1")
                .parentId(UUID.fromString("11111111-1111-1111-1111-111111111114"))
                .leaf(true)
                .build();
    }

    private BulletinCharacteristicData createBulletinCharacteristicData(UUID bulletinId, UUID categoryId) {
        CharacteristicData characteristic = getCharacteristic(categoryId);
        CharacteristicValueData characteristicValue = getCharacteristicValue(characteristic.getId());

        return BulletinCharacteristicData.builder()
                .id(UUID.randomUUID())
                .bulletinId(bulletinId)
                .name(characteristic)
                .value(characteristicValue)
                .build();
    }

    private CharacteristicData getCharacteristic(UUID categoryId) {
        return CharacteristicData.builder()
                .id(UUID.fromString("11111111-1111-1111-1111-111111111115"))
                .name("characteristic 1")
                .categoryId(categoryId)
                .build();
    }

    private CharacteristicValueData getCharacteristicValue(UUID characteristicId) {
            return CharacteristicValueData.builder()
                    .id(UUID.fromString("11111111-1111-1111-1111-111111111116"))
                    .name("characteristic value")
                    .characteristicId(characteristicId)
                    .build();
    }

    private BulletinImageData createBulletinImage(UUID bulletinId) {
        return BulletinImageData.builder()
                .id(UUID.fromString("11111111-1111-1111-1111-111111111117"))
                .bulletinId(bulletinId)
                .imageId(UUID.fromString("11111111-1111-1111-1111-111111111118"))
                .build();
    }

}
