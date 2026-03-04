package com.example.bulletin.unit.domain.vo;

import com.example.bulletin.domain.vo.*;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class BulletinCharacteristicDataTests {

    @Test
    void shouldReturnTrueWhenAllFieldsMatchExceptId() {
        // Arrange
        BulletinCharacteristicData data1 = createDataBuilder()
                .build();

        BulletinCharacteristicData data2 = createDataBuilder()
                .build();

        // Act
        boolean result = data1.equalsData(data2);

        // Assert
        assertTrue(result);
    }

    @Test
    void shouldReturnTrueWhenAllFieldsMatchExceptIdAndNameIsNull() {
        // Arrange
        BulletinCharacteristicData data1 = createDataBuilder()
                .name(null)
                .build();

        BulletinCharacteristicData data2 = createDataBuilder()
                .name(null)
                .build();

        // Act
        boolean result = data1.equalsData(data2);

        // Assert
        assertTrue(result);
    }

    @Test
    void shouldReturnTrueWhenAllFieldsMatchExceptIdAndValueIsNull() {
        // Arrange
        BulletinCharacteristicData data1 = createDataBuilder()
                .value(null)
                .build();

        BulletinCharacteristicData data2 = createDataBuilder()
                .value(null)
                .build();

        // Act
        boolean result = data1.equalsData(data2);

        // Assert
        assertTrue(result);
    }

    private BulletinCharacteristicData.BulletinCharacteristicDataBuilder createDataBuilder() {

        UUID bulletinId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        CharacteristicData characteristic = getCharacteristic();
        CharacteristicValueData characteristicValue = getCharacteristicValue();

        return BulletinCharacteristicData.builder()
                .id(UUID.randomUUID())
                .bulletinId(bulletinId)
                .name(characteristic)
                .value(characteristicValue);
    }

    private CharacteristicData getCharacteristic() {
        UUID categoryId = UUID.fromString("11111111-1111-1111-1111-111111111112");;
        return CharacteristicData.builder()
                .id(UUID.fromString("11111111-1111-1111-1111-111111111113"))
                .name("characteristic 1")
                .categoryId(categoryId)
                .build();
    }

    private CharacteristicValueData getCharacteristicValue() {
        CharacteristicData characteristic = getCharacteristic();
        return CharacteristicValueData.builder()
                .id(UUID.fromString("11111111-1111-1111-1111-111111111114"))
                .name("characteristic value")
                .characteristicId(characteristic.getId())
                .build();
    }

}
