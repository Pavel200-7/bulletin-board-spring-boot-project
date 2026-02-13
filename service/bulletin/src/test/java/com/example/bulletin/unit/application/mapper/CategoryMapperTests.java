package com.example.bulletin.unit.application.mapper;

import com.example.bulletin.application.mapper.CategoryMapper;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.vo.CategoryData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CategoryMapperTests {

    @Autowired
    private CategoryMapper mapper;

    @Test
    public void shouldConvertCorrectlyFromDataToEntity() {
        // Arrange
        Category category = createCategory();
        CategoryData expected = createCategoryData(category);

        // Act
        CategoryData actual = mapper.toData(category);

        // Assert
        assertTrue(expected.equals(actual));
    }

    private Category createCategory() {
        return Category.createRoot("test");
    }

    private CategoryData createCategoryData(Category category) {
        return CategoryData.builder()
                .id(category.getId())
                .name(category.getName())
                .leaf(category.isLeaf())
                .parentId(null)
                .build();
    }

}
