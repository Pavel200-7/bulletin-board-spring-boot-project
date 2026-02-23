package com.example.bulletin.unit.domain.entity.characteristic;

import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CharacteristicCreateTests {

    @Test
    public void shouldCreateCharacteristic() {
        // Arrange
        String name = "characteristic 1";
        Category category = createCategory();

        // Act
        Characteristic characteristic = Characteristic.createCharacteristic(name, category);

        // Assert
        assertEquals(name, characteristic.getName());
        assertEquals(category, characteristic.getCategory());
    }

    public Category createCategory() {
        return Category.createRoot("root");
    }

}
