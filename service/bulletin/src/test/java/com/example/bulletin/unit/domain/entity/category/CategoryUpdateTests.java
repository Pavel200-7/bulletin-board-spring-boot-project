package com.example.bulletin.unit.domain.entity.category;

import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryUpdateTests {

    @Test
    public void shouldRename() {
        // Arrange
        Category category = Category.createRoot("Name 1");
        String newName = "new name";

        // Act
        category.rename(newName);

        // Assert
        assertEquals(newName, category.getName());
    }

    @Test
    public void shouldAddCharacteristic() {
        // Arrange
        Category category = createCategory();
        String characteristicName = "characteristic 1";

        // Act
        category.addCharacteristic(characteristicName);

        // Assert
        assertFalse(category.getCharacteristics()
                .isEmpty());
        assertEquals(characteristicName,
                category.getCharacteristics()
                        .getFirst()
                        .getName());
    }

    @Test
    public void shouldRemoveCharacteristic() {
        // Arrange
        Category category = createCategory();
        category.addCharacteristic("characteristic");
        Characteristic characteristic = category.getCharacteristics().get(0);

        // Act
        category.removeCharacteristic(characteristic);

        // Assert
        assertTrue(category.getCharacteristics()
                .isEmpty());
    }

    @Test
    public void shouldRemoveCharacteristicOnAnotherCategory() {
        // Arrange
        Category category = createCategory();
        category.addCharacteristic("characteristic");

        Category anotherCategory = createCategory();
        Characteristic anotherCharacteristic = anotherCategory.addCharacteristic("another characteristic");

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                category.removeCharacteristic(anotherCharacteristic));
    }

    public Category createCategory() {
        return Category.createRoot("root");
    }

}
