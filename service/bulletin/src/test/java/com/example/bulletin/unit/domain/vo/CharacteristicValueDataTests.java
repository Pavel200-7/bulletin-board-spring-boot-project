package com.example.bulletin.unit.domain.vo;

import com.example.bulletin.domain.vo.CharacteristicValueData;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class CharacteristicValueDataTests {

    @Test
    void shouldReturnTrueWhenAllFieldsMatchExceptId() {
        // Arrange
        UUID characteristicId = UUID.randomUUID();
        CharacteristicValueData data1 = CharacteristicValueData.builder()
                .id(UUID.randomUUID())
                .name("name")
                .characteristicId(characteristicId)
                .build();

        CharacteristicValueData data2 = CharacteristicValueData.builder()
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
