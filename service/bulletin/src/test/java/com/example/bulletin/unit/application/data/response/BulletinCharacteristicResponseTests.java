package com.example.bulletin.unit.application.data.response;

import com.example.bulletin.application.data.response.BulletinCharacteristicResponse;
import com.example.bulletin.application.data.response.CharacteristicResponse;
import com.example.bulletin.application.data.response.CharacteristicValueResponse;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BulletinCharacteristicResponseTests {

    @Test
    void shouldReturnTrueWhenAllFieldsMatchExceptId() {
        // Arrange
        BulletinCharacteristicResponse data1 = createResponseBuilder()
                .build();

        BulletinCharacteristicResponse data2 = createResponseBuilder()
                .build();

        // Act
        boolean result = data1.equalsData(data2);

        // Assert
        assertTrue(result);
    }

    @Test
    void shouldReturnTrueWhenAllFieldsMatchExceptIdAndNameIsNull() {
        // Arrange
        BulletinCharacteristicResponse data1 = createResponseBuilder()
                .name(null)
                .build();

        BulletinCharacteristicResponse data2 = createResponseBuilder()
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
        BulletinCharacteristicResponse data1 = createResponseBuilder()
                .value(null)
                .build();

        BulletinCharacteristicResponse data2 = createResponseBuilder()
                .value(null)
                .build();

        // Act
        boolean result = data1.equalsData(data2);

        // Assert
        assertTrue(result);
    }

    private BulletinCharacteristicResponse.BulletinCharacteristicResponseBuilder createResponseBuilder() {
        UUID bulletinId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        CharacteristicResponse characteristic = createCharacteristic();
        CharacteristicValueResponse characteristicValue = createCharacteristicValue();

        return BulletinCharacteristicResponse.builder()
                .id(UUID.randomUUID())
                .bulletinId(bulletinId)
                .name(characteristic)
                .value(characteristicValue);
    }

    private CharacteristicResponse createCharacteristic() {
        UUID categoryId = UUID.fromString("11111111-1111-1111-1111-111111111112");;
        return CharacteristicResponse.builder()
                .id(UUID.fromString("11111111-1111-1111-1111-111111111113"))
                .name("characteristic 1")
                .categoryId(categoryId)
                .build();
    }

    private CharacteristicValueResponse createCharacteristicValue() {
        CharacteristicResponse characteristic = createCharacteristic();
        return CharacteristicValueResponse.builder()
                .id(UUID.fromString("11111111-1111-1111-1111-111111111114"))
                .name("characteristic value")
                .characteristicId(characteristic.getId())
                .build();
    }

}
