package com.example.bulletin.unit.domain.vo;

import com.example.bulletin.domain.vo.*;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BulletinCharacteristicDataTests {

    UUID bulletinId = null;
    CharacteristicData characteristic = null;
    CharacteristicValueData characteristicValue = null;

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

        UUID bulletinId = getBulletinId();

        CharacteristicData characteristic = getCharacteristic();
        CharacteristicValueData characteristicValue = getCharacteristicValue();

        return BulletinCharacteristicData.builder()
                .id(UUID.randomUUID())
                .bulletinId(bulletinId)
                .name(characteristic)
                .value(characteristicValue);
    }

    private UUID getBulletinId() {
        if (this.bulletinId == null) {
            this.bulletinId = UUID.randomUUID();
        }
        return this.bulletinId;
    }

    private CharacteristicData getCharacteristic() {
        if (this.characteristic == null) {
            UUID categoryId = UUID.randomUUID();
            this.characteristic = CharacteristicData.builder()
                    .id(UUID.randomUUID())
                    .name("characteristic 1")
                    .categoryId(categoryId)
                    .build();
        }
        return this.characteristic;
    }

    private CharacteristicValueData getCharacteristicValue() {
        if (this.characteristicValue == null) {
            CharacteristicData characteristic = getCharacteristic();
            this.characteristicValue = CharacteristicValueData.builder()
                    .id(UUID.randomUUID())
                    .name("characteristic value")
                    .characteristicId(characteristic.getId())
                    .build();
        }
        return this.characteristicValue;
    }

}
