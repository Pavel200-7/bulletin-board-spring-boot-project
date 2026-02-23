package com.example.bulletin.unit.application.mapper;

import com.example.bulletin.application.mapper.CharacteristicValueMapper;
import com.example.bulletin.application.service.characteristicvalue.data.response.data.CharacteristicValueResponse;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import com.example.bulletin.domain.entity.CharacteristicValue;
import com.example.bulletin.domain.vo.CharacteristicValueData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CharacteristicValueMapperTests {

    @Autowired
    private CharacteristicValueMapper mapper;

    @Test
    public void shouldConvertCorrectlyFromEntityToData() {
        // Arrange
        CharacteristicValue characteristicValue = createCharacteristicValue();
        CharacteristicValueData expected = CharacteristicValueData.builder()
                .id(characteristicValue.getId())
                .name(characteristicValue.getName())
                .characteristicId(characteristicValue.getCharacteristic().getId())
                .build();

        // Act
        CharacteristicValueData actual = mapper.toData(characteristicValue);

        // Assert
        assertTrue(expected.equals(actual));
    }

    @Test
    public void shouldConvertCorrectlyFromEntityToResponse() {
        // Arrange
        CharacteristicValue characteristicValue = createCharacteristicValue();
        CharacteristicValueResponse expected = CharacteristicValueResponse.builder()
                .id(characteristicValue.getId())
                .name(characteristicValue.getName())
                .characteristicId(characteristicValue.getCharacteristic().getId())
                .build();

        // Act
        CharacteristicValueResponse actual = mapper.toResponse(characteristicValue);

        // Assert
        assertTrue(expected.equals(actual));
    }

    private CharacteristicValue createCharacteristicValue() {
        Category category = Category.createRoot("test");
        Characteristic characteristic = category.addCharacteristic("characteristic");
        CharacteristicValue characteristicValue = characteristic.addPossibleValue("test value");
        return characteristicValue;
    }

}