package com.example.bulletin.unit.application.data.response;

import com.example.bulletin.application.data.response.CharacteristicResponse;
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
