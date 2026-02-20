package com.example.bulletin.unit.domain.vo;

import com.example.bulletin.domain.vo.CategoryData;
import com.example.bulletin.domain.vo.CharacteristicData;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CharacteristicDataTests {

    @Test
    void shouldReturnTrueWhenAllFieldsMatchExceptId() {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        CharacteristicData data1 = CharacteristicData.builder()
                .id(UUID.randomUUID())
                .name("name")
                .categoryId(categoryId)
                .build();

        CharacteristicData data2 = CharacteristicData.builder()
                .id(UUID.randomUUID())
                .name("name")
                .categoryId(categoryId)
                .build();

        // Act
        boolean result = data1.equalsData(data2);

        // Assert
        assertTrue(result);
    }

}
