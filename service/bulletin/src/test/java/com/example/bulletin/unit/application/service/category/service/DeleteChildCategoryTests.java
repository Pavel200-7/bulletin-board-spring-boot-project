package com.example.bulletin.unit.application.service.category.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.CategoryMapper;
import com.example.bulletin.application.service.category.CategoryServiceImpl;
import com.example.bulletin.application.service.category.data.request.DeleteChildCategoryRequest;
import com.example.bulletin.application.service.category.helper.inter.CategoryFamilyResponseBuilder;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.infrastructure.repository.BulletinRepository;
import com.example.bulletin.infrastructure.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
public class DeleteChildCategoryTests {

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

    @Mock
    private Category category;

    @Mock
    private Category parentCategory;


    @BeforeEach
    public void setup() {
        when(categoryRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(parentCategory));
    }

    @Test
    public void shouldThrowWhenNotFound() {
        // Arrange
        DeleteChildCategoryRequest request = createRequest();
        when(categoryRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {service.deleteChildCategory(request); } );
    }

    @Test
    public void shouldDelete() {
        // Arrange
        DeleteChildCategoryRequest request = createRequest();

        // Act
        service.deleteChildCategory(request);

        // Assert
        verify(parentCategory).removeChild(any(UUID.class));
        verify(categoryRepository).save(parentCategory);
    }

    public DeleteChildCategoryRequest createRequest() {
        return DeleteChildCategoryRequest.builder()
                .parentId(UUID.randomUUID())
                .childId(UUID.randomUUID())
                .build();
    }

}
