package com.example.bulletin.unit.application.service.category.service;

import com.example.bulletin.application.exception.DuplicateResourceException;
import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.CategoryMapper;
import com.example.bulletin.application.service.category.CategoryServiceImpl;
import com.example.bulletin.application.service.category.data.request.CreateChildCategoryRequest;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CreateChildTests {

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
    private Category child = null;

    @BeforeEach
    public void setup() {
        when(repository.existsByNameAndParentId(any(String.class), any(UUID.class)))
                .thenReturn(false);

        Optional<Category> parentCategory = Optional.of(createRootCategory());
        when(repository.findById(any(UUID.class)))
                .thenReturn(parentCategory);

        when(repository.save(any(Category.class)))
                .thenReturn(createChildCategory());

        when(mapper.toResponse(any(Category.class)))
                .thenReturn(mapperHelper.toResponse(createChildCategory()));
    }

    @Test
    public void shouldThrowWhenParentHasChildWithSuchName() {
        // Arrange
        CreateChildCategoryRequest request = createCreateChildRequest();
        when(repository.existsByNameAndParentId(any(String.class), any(UUID.class)))
                .thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> { service.createChild(request); });
    }

    @Test
    public void shouldThrowWhenParentDoesNotExist() {
        // Arrange
        CreateChildCategoryRequest request = createCreateChildRequest();
        Optional<Category> parentCategory = Optional.empty();
        when(repository.findById(any(UUID.class)))
                .thenReturn(parentCategory);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> { service.createChild(request); });
    }

    @Test
    public void shouldCreateChildAndSave() {
        // Arrange
        CreateChildCategoryRequest request = createCreateChildRequest();
        Category expected = createChildCategory();

        // Act
        service.createChild(request);

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
        CreateChildCategoryRequest request = createCreateChildRequest();
        CategoryResponse expected = mapperHelper.toResponse(createChildCategory());

        // Act
        var response = service.createChild(request);
        CategoryResponse actual = response.getCategoryResponse();

        // Assert
        assertTrue(expected.equalsData(actual));
    }

    public Category createChildCategory() {
        if (child == null) {
            Category root = createRootCategory();
            CreateChildCategoryRequest request = createCreateChildRequest();
            child = root.createChild(request.getName());
        }
        return child;
    }

    public Category createRootCategory() {
        if (root == null) {
            root = Category.createRoot("root");
        }
        return root;
    }

    public CreateChildCategoryRequest createCreateChildRequest() {
        Category root = createRootCategory();
        return CreateChildCategoryRequest.builder()
                .parentId(root.getId())
                .name("child")
                .build();
    }
}
