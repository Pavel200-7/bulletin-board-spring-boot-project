package com.example.bulletin.unit.domain.entity.category;

import com.example.bulletin.application.mapper.CategoryMapper;
import com.example.bulletin.domain.entity.Category;
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

}
