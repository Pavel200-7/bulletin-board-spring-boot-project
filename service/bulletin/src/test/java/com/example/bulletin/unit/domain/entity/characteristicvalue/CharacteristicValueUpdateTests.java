package com.example.bulletin.unit.domain.entity.characteristicvalue;

import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import com.example.bulletin.domain.entity.CharacteristicValue;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
public class CharacteristicValueUpdateTests {

    @Test
    public void shouldRename() {
        // Arrange
        Category category = Category.createRoot("root");
        Characteristic characteristic = category.addCharacteristic("characteristic");
        CharacteristicValue characteristicValue = characteristic.addPossibleValue("initial value");
        String newName = "updated value";


        // Act
        CharacteristicValue renamedValue = characteristicValue.rename(newName);

        // Assert
        assertEquals(newName, renamedValue.getName());
    }

}