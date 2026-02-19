package com.example.bulletin.unit.domain.entity.characteristicvalue;

import com.example.bulletin.application.mapper.CharacteristicValueMapper;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import com.example.bulletin.domain.entity.CharacteristicValue;
import com.example.bulletin.domain.vo.CharacteristicValueData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CharacteristicValueUpdateTests {

    @Autowired
    private CharacteristicValueMapper mapper;

    @Test
    public void shouldRename() {
        // Arrange
        Category category = Category.createRoot("root");
        Characteristic characteristic = category.addCharacteristic("characteristic");
        CharacteristicValue characteristicValue = characteristic.addPossibleValue("initial value");
        String newName = "updated value";

        CharacteristicValueData expected = CharacteristicValueData.builder()
                .name(newName)
                .characteristicId(characteristic.getId())
                .build();

        // Act
        CharacteristicValue renamedValue = characteristicValue.rename(newName);
        CharacteristicValueData actual = mapper.toData(renamedValue);

        // Assert
        assertEquals(expected.getName(), actual.getName());
    }

}