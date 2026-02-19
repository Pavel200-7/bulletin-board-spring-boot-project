package com.example.bulletin.unit.domain.entity.characteristicvalue;

import com.example.bulletin.application.mapper.CharacteristicValueMapper;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import com.example.bulletin.domain.entity.CharacteristicValue;
import com.example.bulletin.domain.vo.CharacteristicValueData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CharacteristicValueCreateTests {

    @Autowired
    private CharacteristicValueMapper mapper;

    @Test
    public void shouldCreateCharacteristicValue() {
        // Arrange
        String characteristicValueName = "value 1";
        Category category = createCategory();
        Characteristic characteristic = category.addCharacteristic("characteristic 1");

        CharacteristicValueData expected = CharacteristicValueData.builder()
                .id(UUID.randomUUID())
                .name(characteristicValueName)
                .characteristicId(characteristic.getId())
                .build();

        // Act
        CharacteristicValue characteristicValue = CharacteristicValue.createCharacteristicValue(
                characteristicValueName,
                characteristic
        );
        CharacteristicValueData actual = mapper.toData(characteristicValue);

        // Assert
        assertTrue(expected.equalsData(actual));
    }

    private Category createCategory() {
        return Category.createRoot("root");
    }

}
