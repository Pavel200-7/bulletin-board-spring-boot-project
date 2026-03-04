package com.example.bulletin.unit.domain.entity.category;

import com.example.bulletin.domain.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class CategoryDeleteTests {

    @Test
    public void shouldRemoveChildWhenNoChildren() {
        // Arrange
        Category parentCategory = createCategoryWithChildren();
        Category child = parentCategory.getChildren()
                .getFirst();
        UUID childId = child.getId();

        // Act
        parentCategory.removeChild(childId);

        // Assert
        assertTrue(parentCategory.getChildren().isEmpty());
    }

    @Test
    public void shouldThrowWhenHasChildren() {
        // Arrange
        Category parentCategory = createCategoryWithChildren();
        Category child = parentCategory.getChildren()
                .getFirst();
        child.createLeafyChild("2 level child");
        UUID childId = child.getId();

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> { parentCategory.removeChild(childId); });
    }

    @Test
    public void shouldDelete() {
        // Arrange
        Category root = Category.createRoot("root");

        // Act & Assert
        assertDoesNotThrow(() -> { root.delete(); } );
    }

    @Test
    public void shouldThrowWhenRootHasChildren() {
        // Arrange
        Category root = createCategoryWithChildren();

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> { root.delete(); });
    }

    public Category createCategoryWithChildren() {
        Category root = Category.createRoot("root");
        root.createChild("child 1");
        return root;
    }

}
