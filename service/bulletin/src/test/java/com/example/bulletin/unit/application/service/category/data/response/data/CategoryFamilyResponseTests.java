package com.example.bulletin.unit.application.service.category.data.response.data;

import com.example.bulletin.application.service.category.data.response.data.CategoryFamilyResponse;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CategoryFamilyResponseTests {

    @Test
    void shouldReturnTrueWhenAllFieldsMatchExceptId() {
        // Arrange
        CategoryFamilyResponse data1 = CategoryFamilyResponse.builder()
                .id(UUID.randomUUID())
                .name("Electronics")
                .leaf(false)
                .parentId(null)
                .build();

        CategoryFamilyResponse data2 = CategoryFamilyResponse.builder()
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
