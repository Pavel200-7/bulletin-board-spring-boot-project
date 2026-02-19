package com.example.bulletin.unit.domain.entity.characteristic;

import com.example.bulletin.application.mapper.CharacteristicMapper;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import com.example.bulletin.domain.vo.CharacteristicData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CharacteristicCreateTests {

    @Autowired
    private CharacteristicMapper mapper;

    @Test
    public void shouldCreateCharacteristic() {
        // Arrange
        String characteristicName = "characteristic 1";
        Category category = createCategory();
        CharacteristicData expected = CharacteristicData.builder()
                .id(UUID.randomUUID())
                .name(characteristicName)
                .categoryId(category.getId())
                .build();


        // Act
        Characteristic characteristic = Characteristic.createCharacteristic(characteristicName, category);
        CharacteristicData actual = mapper.toData(characteristic);

        // Assert
        assertTrue(expected.equalsData(actual));
    }

    public Category createCategory() {
        return Category.createRoot("root");
    }

}
