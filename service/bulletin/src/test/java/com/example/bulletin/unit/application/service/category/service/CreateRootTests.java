package com.example.bulletin.unit.application.service.category.service;

import com.example.bulletin.application.exception.DuplicateResourceException;
import com.example.bulletin.application.mapper.CategoryMapper;
import com.example.bulletin.application.service.category.CategoryServiceImpl;
import com.example.bulletin.application.service.category.data.request.CreateChildCategoryRequest;
import com.example.bulletin.application.service.category.data.request.CreateRootCategoryRequest;
import com.example.bulletin.application.service.category.data.response.data.CategoryResponse;
import com.example.bulletin.domain.entity.Category;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CreateRootTests {

    @Autowired
    private CategoryMapper mapperHelper;

    @Mock
    private CategoryRepository repository;

    @Mock
    private CategoryMapper mapper;

    @InjectMocks
    private CategoryServiceImpl service;

    @Captor
    private ArgumentCaptor<Category> categoryCaptor;

    private Category root = null;

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
    public void shouldCreateRootAndSave() {
        CreateRootCategoryRequest request = createCreateRootRequest();
        Category expected = createRootCategory();


        // Act
        service.createRoot(request);

        // Assert
        verify(repository).save(categoryCaptor.capture());
        Category actual = categoryCaptor.getValue();

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    public void shouldReturnMappedCategory() {
        // Arrange
        CreateRootCategoryRequest request = createCreateRootRequest();
        CategoryResponse expected = mapperHelper.toResponse(createRootCategory());

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

        if (root == null) {
            var request = createCreateRootRequest();
            root = Category.createRoot(request.getName());
        }
        return root;
    }
}
