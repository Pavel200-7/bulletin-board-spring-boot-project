package com.example.bulletin.unit.application.service.category.service;

import com.example.bulletin.application.exception.DuplicateResourceException;
import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.CategoryMapper;
import com.example.bulletin.application.service.category.CategoryServiceImpl;
import com.example.bulletin.application.service.category.data.request.CreateChildCategoryRequest;
import com.example.bulletin.application.service.category.data.request.CreateLeafyChildCategoryRequest;
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
public class CreateLeafyChildTests {

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
                .thenReturn(createLeafyChildCategory());

        when(mapper.toResponse(any(Category.class)))
                .thenReturn(mapperHelper.toResponse(createLeafyChildCategory()));
    }

    @Test
    public void shouldThrowWhenParentHasChildWithSuchName() {
        // Arrange
        CreateLeafyChildCategoryRequest request = createCreateLeafyChildRequest();
        when(repository.existsByNameAndParentId(any(String.class), any(UUID.class)))
                .thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> { service.createLeafyChild(request); });
    }

    @Test
    public void shouldThrowWhenParentDoesNotExist() {
        // Arrange
        CreateLeafyChildCategoryRequest request = createCreateLeafyChildRequest();
        Optional<Category> parentCategory = Optional.empty();
        when(repository.findById(any(UUID.class)))
                .thenReturn(parentCategory);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> { service.createLeafyChild(request); });
    }

    @Test
    public void shouldCreateChildAndSave() {
        // Arrange
        CreateLeafyChildCategoryRequest request = createCreateLeafyChildRequest();
        Category expected = createLeafyChildCategory();

        // Act
        service.createLeafyChild(request);

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
        CreateLeafyChildCategoryRequest request = createCreateLeafyChildRequest();
        CategoryResponse expected = mapperHelper.toResponse(createLeafyChildCategory());

        // Act
        var response = service.createLeafyChild(request);
        CategoryResponse actual = response.getCategoryResponse();

        // Assert
        assertTrue(expected.equalsData(actual));
    }

    public Category createLeafyChildCategory() {
        if (child == null) {
            Category root = createRootCategory();
            CreateLeafyChildCategoryRequest request = createCreateLeafyChildRequest();
            child = root.createLeafyChild(request.getName());
        }
        return child;
    }

    public Category createRootCategory() {
        if (root == null) {
            root = Category.createRoot("root");
        }
        return root;
    }

    public CreateLeafyChildCategoryRequest createCreateLeafyChildRequest() {
        Category root = createRootCategory();
        return CreateLeafyChildCategoryRequest.builder()
                .parentId(root.getId())
                .name("child")
                .build();
    }
}
