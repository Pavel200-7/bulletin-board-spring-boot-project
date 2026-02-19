package com.example.bulletin.unit.application.service.characteristic.data.response.data;

import com.example.bulletin.application.service.characteristic.data.response.data.CharacteristicResponse;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CharacteristicResponseTests {

    @Test
    void shouldReturnTrueWhenAllFieldsMatchExceptId() {
        // Arrange
        CharacteristicResponse data1 = CharacteristicResponse.builder()
                .id(UUID.randomUUID())
                .name("color")
                .categoryId(null)
                .build();

        CharacteristicResponse data2 = CharacteristicResponse.builder()
                .id(UUID.randomUUID())
                .name("color")
                .categoryId(null)
                .build();

        // Act
        boolean result = data1.equalsData(data2);

        // Assert
        assertTrue(result);
    }
}
