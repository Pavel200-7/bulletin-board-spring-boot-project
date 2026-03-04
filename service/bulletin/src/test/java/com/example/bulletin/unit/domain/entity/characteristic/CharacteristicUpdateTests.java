package com.example.bulletin.unit.domain.entity.characteristic;

import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import com.example.bulletin.domain.entity.CharacteristicValue;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class CharacteristicUpdateTests {

    @Test
    public void shouldRename() {
        // Arrange
        Category category = Category.createRoot("Name 1");
        Characteristic characteristic = category.addCharacteristic("characteristic");
        String newName = "new name";

        // Act
        characteristic.rename(newName);

        // Assert
        assertEquals(newName, characteristic.getName());
    }

    @Test
    public void shouldAddPossibleValue() {
        // Arrange
        Category category = Category.createRoot("root");
        Characteristic characteristic = category.addCharacteristic("characteristic 1");
        String valueName = "possible value 1";

        // Act
        characteristic.addPossibleValue(valueName);

        // Assert
        assertFalse(characteristic.getPossibleValues().isEmpty());
        assertEquals(valueName,
                characteristic.getPossibleValues()
                        .getFirst()
                        .getName());
    }

    @Test
    public void shouldRemovePossibleValue() {
        // Arrange
        Category category = Category.createRoot("root");
        Characteristic characteristic = category.addCharacteristic("characteristic 1");
        String valueName = "possible value 1";
        CharacteristicValue value = characteristic.addPossibleValue(valueName);

        // Act
        characteristic.removePossibleValue(value.getId());

        // Assert
        assertTrue(characteristic.getPossibleValues()
                .isEmpty());
    }

    @Test
    public void shouldThrowWhenPossibleValueIsOfAnotherCharacteristic() {
        // Arrange
        Category category = Category.createRoot("root");
        Characteristic characteristic = category.addCharacteristic("characteristic 1");

        Characteristic anotherCharacteristic = category.addCharacteristic("another characteristic");
        String anotherValueName = "possible value 1";
        CharacteristicValue anotherCharacteristicValue = anotherCharacteristic.addPossibleValue(anotherValueName);

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                characteristic.removePossibleValue(anotherCharacteristicValue.getId()));
    }

}
