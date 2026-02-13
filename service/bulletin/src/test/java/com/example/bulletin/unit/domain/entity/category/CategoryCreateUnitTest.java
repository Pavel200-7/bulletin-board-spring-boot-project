package com.example.bulletin.unit.domain.entity.category;

import com.example.bulletin.application.mapper.CategoryMapper;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.vo.CategoryData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CategoryCreateUnitTest {

    @Autowired
    private CategoryMapper mapper;

    @Test
    public void shouldCreateRootCategory() {
        // Arrange
        String name = "root";
        CategoryData expected = expectedChildData(name, null, false);

        // Act
        Category category = Category.createRoot(name);
        CategoryData actual = mapper.toData(category);

        // Assert
        assertTrue(expected.equalsData(actual));
    }

    @Test
    public void shouldCreateChildCategory() {
        // Arrange
        Category parent = Category.createRoot("root");
        String name = "child";
        CategoryData expected = expectedChildData(name, parent.getId(), false);

        // Act
        Category category = parent.createChild(name);
        CategoryData actual = mapper.toData(category);

        // Assert
        assertTrue(expected.equalsData(actual));
        assertFalse(parent.getChildren().isEmpty());
    }

    @Test
    public void shouldCreateLeafyChildCategory() {
        // Arrange
        Category parent = Category.createRoot("root");
        String name = "child";
        CategoryData expected = expectedChildData(name, parent.getId(), true);

        // Act
        Category category = parent.createLeafyChild(name);
        CategoryData actual = mapper.toData(category);

        // Assert
        assertTrue(expected.equalsData(actual));
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

    private CategoryData expectedChildData(String name, UUID parentId, boolean isLeaf) {
        return CategoryData.builder()
                .id(null)
                .name(name)
                .parentId(parentId)
                .leaf(isLeaf)
                .build();
    }

    public Category createLeafyParent() {
        Category root = Category.createRoot("root");
        Category leaf = root.createLeafyChild("leaf");
        return leaf;
    }

}
