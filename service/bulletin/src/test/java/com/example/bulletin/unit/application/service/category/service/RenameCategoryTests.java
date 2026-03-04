package com.example.bulletin.unit.application.service.category.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.CategoryMapper;
import com.example.bulletin.application.service.category.CategoryServiceImpl;
import com.example.bulletin.application.service.category.data.request.RenameCategoryRequest;
import com.example.bulletin.application.data.response.CategoryResponse;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RenameCategoryTests {

    @Autowired
    private CategoryMapper mapperHelper;

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

    private Category category = null;

    @BeforeEach
    public void setup() {
        Optional<Category> category = Optional.of(createCategory());
        when(categoryRepository.findById(any(UUID.class)))
                .thenReturn(category);


        Category renamedCategory = createRenamedCategory();
        when(categoryRepository.save(any(Category.class)))
                .thenReturn(renamedCategory);

        when(mapper.toResponse(any(Category.class)))
                .thenReturn(mapperHelper.toResponse(renamedCategory));
    }

    @Test
    public void shouldThrowWhenNotFound() {
        // Arrange
        RenameCategoryRequest request = createRequest();
        when(categoryRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {service.renameCategory(request); } );
    }

    @Test
    public void shouldRenameAndSave() {
        // Arrange
        RenameCategoryRequest request = createRequest();
        Category expected = createCategory()
                .rename(request.getName());

        // Act
        service.renameCategory(request);

        // Assert
        verify(categoryRepository).save(categoryCaptor.capture());
        Category actual = categoryCaptor.getValue();

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    public void shouldReturnMappedCategory() {
        // Arrange
        RenameCategoryRequest request = createRequest();
        CategoryResponse expected = mapperHelper.toResponse(createRenamedCategory());

        // Act
        var response = service.renameCategory(request);
        CategoryResponse actual = response.getCategoryResponse();

        // Assert
        assertTrue(expected.equalsData(actual));
    }

    public Category createCategory() {
        if (category == null) {
            category = Category.createRoot("root");
        }
        return category;
    }

    public Category createRenamedCategory() {
        RenameCategoryRequest request = createRequest();
        Category category = createCategory();
        return category.rename(request.getName());
    }

    public RenameCategoryRequest createRequest() {
        return RenameCategoryRequest.builder()
                .id(UUID.randomUUID())
                .name("new name")
                .build();
    }

}
