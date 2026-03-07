package com.example.bulletin.unit.application.mapper;

import com.example.bulletin.application.mapper.CategoryMapper;
import com.example.bulletin.application.data.response.CategoryResponse;
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

    private Category createCategory() {
         return Category.createRoot("test");
    }

}
