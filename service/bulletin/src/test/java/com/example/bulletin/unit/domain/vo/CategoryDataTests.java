package com.example.bulletin.unit.domain.vo;

import com.example.bulletin.domain.vo.CategoryData;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CategoryDataTests {

    @Test
    void shouldReturnTrueWhenAllFieldsMatchExceptId() {
        // Arrange
        CategoryData data1 = CategoryData.builder()
                .id(UUID.randomUUID())
                .name("Electronics")
                .leaf(false)
                .parentId(null)
                .build();

        CategoryData data2 = CategoryData.builder()
                .id(UUID.randomUUID())
                .name("Electronics")
                .leaf(false)
                .parentId(null)
                .build();

        // Act
        boolean result = data1.equalsData(data2);

        // Assert
        assertTrue(result);
    }

}
