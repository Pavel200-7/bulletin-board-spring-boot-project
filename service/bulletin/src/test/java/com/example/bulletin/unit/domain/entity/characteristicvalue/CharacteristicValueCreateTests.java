package com.example.bulletin.unit.domain.entity.characteristicvalue;

import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import com.example.bulletin.domain.entity.CharacteristicValue;
import com.example.bulletin.domain.vo.CharacteristicValueData;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
public class CharacteristicValueCreateTests {

    @Test
    public void shouldCreateCharacteristicValue() {
        // Arrange
        String name = "value 1";
        Category category = createCategory();
        Characteristic characteristic = category.addCharacteristic("characteristic 1");

        CharacteristicValueData expected = CharacteristicValueData.builder()
                .id(UUID.randomUUID())
                .name(name)
                .characteristicId(characteristic.getId())
                .build();

        // Act
        CharacteristicValue characteristicValue = CharacteristicValue.createCharacteristicValue(
                name,
                characteristic
        );

        // Assert
        assertEquals(name, characteristicValue.getName());
        assertEquals(characteristic, characteristicValue.getCharacteristic());
    }

    private Category createCategory() {
        return Category.createRoot("root");
    }

}
