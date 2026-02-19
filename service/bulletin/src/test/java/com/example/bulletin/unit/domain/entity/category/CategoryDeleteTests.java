package com.example.bulletin.unit.domain.entity.category;

import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class CategoryDeleteTests {

    @Test
    public void shouldApproveWhenNoChildren() {
        // Arrange
        Category parentCategory = createCategoryWithChildren();
        parentCategory.getChildren().clear();
        Category expected = parentCategory;

        // Act
        Category actual = parentCategory.delete();

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);

    }

    @Test
    public void shouldThrowWhenHasChildren() {
        // Arrange
        Category parentCategory = createCategoryWithChildren();

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> { parentCategory.delete(); });
    }

    @Test
    public void shouldThrowWhenLeafy() {
        // Arrange
        Category parentCategory = createCategoryWithChildren();
        Category leafyChild = parentCategory.getChildren().get(0);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> { leafyChild.delete(); });
    }

    @Test
    public void shouldApproveWhenLeafy() {
        // Arrange
        Category parentCategory = createCategoryWithChildren();
        Category leafyChild = parentCategory.getChildren().get(0);
        Category expected = leafyChild;


        // Act
        Category actual = leafyChild.deleteLeaf();

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);

    }

    @Test
    public void shouldThrowWhenNotLeafy() {
        // Arrange
        Category parentCategory = createCategoryWithChildren();
        Category notLeafyChild = parentCategory.createChild("not leafy");

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> { notLeafyChild.deleteLeaf(); });
    }

    @Test
    public void shouldRemoveCharacteristic() {
        // Arrange
        Category category = createCategoryWithChildren();
        category.addCharacteristic("characteristic");
        Characteristic characteristic = category.getCharacteristics().get(0);

        // Act
        category.removeCharacteristic(characteristic);

        // Assert
        assertTrue(category.getCharacteristics().isEmpty());
    }

    public Category createCategoryWithChildren() {
        Category root = Category.createRoot("root");
        root.createLeafyChild("child 1");
        return root;
    }

}
