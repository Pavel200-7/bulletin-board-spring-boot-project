package com.example.bulletin.unit.application.service.characteristicvalue.data.response.data;

import com.example.bulletin.application.service.characteristicvalue.data.response.data.CharacteristicValueResponse;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CharacteristicValueResponseTests {

    @Test
    void shouldReturnTrueWhenAllFieldsMatchExceptId() {
        // Arrange
        UUID characteristicId = UUID.randomUUID();
        CharacteristicValueResponse data1 = CharacteristicValueResponse.builder()
                .id(UUID.randomUUID())
                .name("name")
                .characteristicId(characteristicId)
                .build();

        CharacteristicValueResponse data2 = CharacteristicValueResponse.builder()
                .id(UUID.randomUUID())
                .name("name")
                .characteristicId(characteristicId)
                .build();

        // Act
        boolean result = data1.equalsData(data2);

        // Assert
        assertTrue(result);
    }
}
