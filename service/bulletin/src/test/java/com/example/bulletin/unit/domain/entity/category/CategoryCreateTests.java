package com.example.bulletin.unit.domain.entity.category;

import com.example.bulletin.domain.entity.Category;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryCreateTests {

    @Test
    public void shouldCreateRootCategory() {
        // Arrange
        String name = "root";

        // Act
        Category category = Category.createRoot(name);

        // Assert
        assertEquals(name, category.getName());
        assertNull(category.getParent());
        assertFalse(category.isLeaf());
    }

    @Test
    public void shouldCreateChildCategory() {
        // Arrange
        Category parent = Category.createRoot("root");
        String name = "child";

        // Act
        Category category = parent.createChild(name);

        // Assert
        assertEquals(name, category.getName());
        assertEquals(parent, category.getParent());
        assertFalse(category.isLeaf());

        assertFalse(parent.getChildren().isEmpty());
    }

    @Test
    public void shouldCreateLeafyChildCategory() {
        // Arrange
        Category parent = Category.createRoot("root");
        String name = "child";

        // Act
        Category category = parent.createLeafyChild(name);

        // Assert
        assertEquals(name, category.getName());
        assertEquals(parent, category.getParent());
        assertTrue(category.isLeaf());

        assertFalse(parent.getChildren().isEmpty());
    }

    @Test
    public void shouldThrowWhenCreateChildAndParentIsLeaf() {
        // Arrange
        Category leafCategory = createLeafyParent();

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> { leafCategory.createChild("child"); });
    }

    @Test
    public void shouldThrowWhenCreateLeafChildAndParentIsLeaf() {
        // Arrange
        Category leafCategory = createLeafyParent();

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> { leafCategory.createLeafyChild("child"); });
    }

    public Category createLeafyParent() {
        Category root = Category.createRoot("root");
        Category leaf = root.createLeafyChild("leaf");
        return leaf;
    }

}
