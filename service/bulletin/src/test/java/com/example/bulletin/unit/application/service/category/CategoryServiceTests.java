package com.example.bulletin.unit.application.service.category;

import com.example.bulletin.application.exception.DuplicateResourceException;
import com.example.bulletin.application.mapper.CategoryMapper;
import com.example.bulletin.application.service.category.CategoryServiceImpl;
import com.example.bulletin.application.service.category.data.request.CreateRootCategoryRequest;
import com.example.bulletin.application.service.category.data.response.data.CategoryResponse;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.infrastructure.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CategoryServiceTests {

    @Autowired
    private CategoryMapper mapperHelper;

    @Mock
    private CategoryRepository repository;

    @Mock
    private CategoryMapper mapper;

    @InjectMocks
    private CategoryServiceImpl service;

    @BeforeEach
    public void setup() {
        when(repository.existsByNameAndParentId(any(String.class), any(UUID.class)))
                .thenReturn(false);

        var rootCategory = createRootCategory();
        when(repository.save(any(Category.class)))
                .thenReturn(rootCategory);

        when(mapper.toResponse(any(Category.class)))
                .thenReturn(mapperHelper.toResponse(rootCategory));
    }

    @Test
    public void shouldThrowWhenParentHasChildWithSuchName() {
        // Arrange
        CreateRootCategoryRequest request = createCreateRootRequest();
        when(repository.existsByNameAndParentId(any(String.class), eq(null)))
                .thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> { service.createRoot(request); });
    }

    @Test
    public void shouldCreateRootAndSaveIt() {
        CreateRootCategoryRequest request = createCreateRootRequest();

        // Act
        service.createRoot(request);

        // Assert
        verify(repository, times(1)).save(any(Category.class));
    }

    @Test
    public void shouldReturnMappedCategory() {
        // Arrange
        CreateRootCategoryRequest request = createCreateRootRequest();
        CategoryResponse expected = createCategoryResponse(createRootCategory());

        // Act
        var response = service.createRoot(request);
        CategoryResponse actual = response.getCategoryResponse();

        // Assert
        assertTrue(expected.equalsData(actual));
    }

    public CreateRootCategoryRequest createCreateRootRequest() {
        return CreateRootCategoryRequest.builder()
                .name("root")
                .build();
    }

    public Category createRootCategory() {
        var request = createCreateRootRequest();
        return Category.createRoot(request.getName());
    }

    public CategoryResponse createCategoryResponse(Category category) {
        return mapper.toResponse(category);
    }

}
