package com.example.bulletin.unit.application.data.response;

import com.example.bulletin.application.data.response.CharacteristicValueResponse;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
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
