package com.example.bulletin.unit.application.mapper;

import com.example.bulletin.application.mapper.CategoryMapper;
import com.example.bulletin.application.data.response.CategoryResponse;
import com.example.bulletin.application.service.category.data.response.data.CategoryWithChildrenResponse;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.vo.CategoryData;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class CategoryMapperTests {

    private CategoryMapper mapper = Mappers.getMapper(
            CategoryMapper.class);

    @Test
    public void shouldConvertCorrectlyFromEntityToData() {
        // Arrange
        Category category = createCategory();
        CategoryData expected = CategoryData.builder()
                .id(category.getId())
                .name(category.getName())
                .leaf(category.isLeaf())
                .parentId(null)
                .build();

        // Act
        CategoryData actual = mapper.toData(category);

        // Assert
        assertTrue(expected.equals(actual));
    }

    @Test
    public void shouldConvertCorrectlyFromEntityToResponse() {
        // Arrange
        Category category = createCategory();
        CategoryResponse expected = CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .leaf(category.isLeaf())
                .parentId(null)
                .build();

        // Act
        CategoryResponse actual = mapper.toResponse(category);

        // Assert
        assertTrue(expected.equals(actual));
    }

    @Test
    void shouldMapCategoryToWithChildrenResponse() {
        // Arrange
        Category root = Category.createRoot("Root Category");
        Category child1 = root.createChild("Child 1");
        Category child2 = root.createChild("Child 2");
        Category leaf = root.createLeafyChild("Leaf Category");

        // Act
        CategoryWithChildrenResponse response = mapper.toWithChildrenResponse(root);

        // Assert
        assertNotNull(response);
        assertEquals(root.getId(), response.getId());
        assertEquals("Root Category", response.getName());
        assertFalse(response.isLeaf());
        assertNull(response.getParentId());

        assertNotNull(response.getChildren());
        assertEquals(3, response.getChildren().size());

        CategoryResponse firstChild = response.getChildren().get(0);
        assertEquals(child1.getId(), firstChild.getId());
        assertEquals("Child 1", firstChild.getName());
        assertEquals(root.getId(), firstChild.getParentId());

        CategoryResponse secondChild = response.getChildren().get(1);
        assertEquals(child2.getId(), secondChild.getId());
        assertEquals("Child 2", secondChild.getName());

        CategoryResponse leafChild = response.getChildren().get(2);
        assertEquals(leaf.getId(), leafChild.getId());
        assertEquals("Leaf Category", leafChild.getName());
        assertTrue(leafChild.isLeaf());
    }

    private Category createCategory() {
         return Category.createRoot("test");
    }

}
