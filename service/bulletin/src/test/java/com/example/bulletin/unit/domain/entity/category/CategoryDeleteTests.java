package com.example.bulletin.unit.domain.entity.category;

import com.example.bulletin.domain.entity.Category;
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

    public Category createCategoryWithChildren() {
        Category root = Category.createRoot("root");
        root.createLeafyChild("child 1");
        return root;
    }

}
