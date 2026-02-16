package com.example.bulletin.unit.application.service.category.service;


import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.CategoryMapper;
import com.example.bulletin.application.service.category.CategoryServiceImpl;
import com.example.bulletin.application.service.category.data.request.DeleteLeafCategoryRequest;
import com.example.bulletin.application.service.category.helper.inter.CategoryFamilyResponseBuilder;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.infrastructure.repository.BulletinRepository;
import com.example.bulletin.infrastructure.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DeleteLeafCategoryTests {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BulletinRepository bulletinRepository;

    @Mock
    private CategoryFamilyResponseBuilder responseBuilder;

    @Mock
    private CategoryMapper mapper;

    @InjectMocks
    private CategoryServiceImpl service;

    @Captor
    private ArgumentCaptor<Category> categoryCaptor;

    @Mock
    private Category category;

    @BeforeEach
    public void setup() {
        when(categoryRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(category));

        when(bulletinRepository.existsByCategoryId(any(UUID.class)))
                .thenReturn(false);
    }

    @Test
    public void shouldThrowWhenNotFound() {
        // Arrange
        DeleteLeafCategoryRequest request = createRequest();
        when(categoryRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {service.deleteLeafCategory(request); } );
    }

    @Test
    public void shouldThrowWhenExistConnectedBulletins() {
        // Arrange
        DeleteLeafCategoryRequest request = createRequest();
        when(bulletinRepository.existsByCategoryId(any(UUID.class)))
                .thenReturn(true);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {service.deleteLeafCategory(request); } );
    }

    @Test
    public void shouldDelete() {
        // Arrange
        DeleteLeafCategoryRequest request = createRequest();

        // Act
        service.deleteLeafCategory(request);

        // Assert
        verify(category).deleteLeaf();
        verify(categoryRepository).delete(category);
    }

    public DeleteLeafCategoryRequest createRequest() {
        return DeleteLeafCategoryRequest.builder()
                .id(UUID.randomUUID())
                .build();
    }

}
