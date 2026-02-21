package com.example.bulletin.unit.domain.entity.category;

import com.example.bulletin.application.mapper.CategoryMapper;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import com.example.bulletin.domain.vo.CategoryData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CategoryUpdateTests {

    @Autowired
    private CategoryMapper mapper;

    @Test
    public void shouldRename() {
        // Arrange
        Category category = Category.createRoot("Name 1");
        String newName = "new name";
        CategoryData expected = CategoryData.builder()
                .name(newName)
                .parentId(null)
                .leaf(false)
                .build();

        // Act
        Category renamedCategory = category.rename(newName);
        CategoryData actual = mapper.toData(renamedCategory);

        // Assert
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    public void shouldAddCharacteristic() {
        // Arrange
        Category category = createCategory();
        String characteristicName = "characteristic 1";

        // Act
        category.addCharacteristic(characteristicName);

        // Assert
        assertFalse(category.getCharacteristics().isEmpty());
        assertEquals(characteristicName,
                category.getCharacteristics().get(0).getName());
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
        assertTrue(category.getCharacteristics().isEmpty());
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
