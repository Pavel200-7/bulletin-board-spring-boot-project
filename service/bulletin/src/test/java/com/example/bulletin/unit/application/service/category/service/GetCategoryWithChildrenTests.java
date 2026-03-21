package com.example.bulletin.unit.application.service.category.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.CategoryMapper;
import com.example.bulletin.application.service.category.CategoryServiceImpl;
import com.example.bulletin.application.service.category.data.request.GetCategoryWithChildrenRequest;
import com.example.bulletin.application.service.category.data.response.data.CategoryWithChildrenResponse;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.infrastructure.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class GetCategoryWithChildrenTests {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper mapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private CategoryMapper mapperHelper = Mappers.getMapper(CategoryMapper.class);

    private Category category;
    private CategoryWithChildrenResponse expectedResponse;

    @BeforeEach
    void setUp() {
        category = Category.createRoot("Test Category");
        expectedResponse = mapperHelper.toWithChildrenResponse(category);

        when(categoryRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(category));

    }

    @Test
    void shouldGetCategoryWithChildren() {
        // Arrange
        UUID existentId = UUID.randomUUID();
        GetCategoryWithChildrenRequest request = new GetCategoryWithChildrenRequest(existentId);

        when(mapper.toWithChildrenResponse(category)).thenReturn(expectedResponse);

        // Act
        var response = categoryService.getCategoryWithChildren(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getCategoryWithChildrenResponse());
        assertEquals("Test Category", response.getCategoryWithChildrenResponse().getName());

        verify(categoryRepository).findById(existentId);
        verify(mapper).toWithChildrenResponse(category);
    }

    @Test
    void shouldThrowWhenCategoryNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        GetCategoryWithChildrenRequest request = new GetCategoryWithChildrenRequest(nonExistentId);

        when(categoryRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.getCategoryWithChildren(request));
    }
}