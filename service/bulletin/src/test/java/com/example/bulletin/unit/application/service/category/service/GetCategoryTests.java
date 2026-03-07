package com.example.bulletin.unit.application.service.category.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.CategoryMapper;
import com.example.bulletin.application.service.category.CategoryServiceImpl;
import com.example.bulletin.application.service.category.data.request.GetCategoryRequest;
import com.example.bulletin.application.data.response.CategoryResponse;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.infrastructure.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GetCategoryTests {

    private CategoryMapper mapperHelper = Mappers.getMapper(
            CategoryMapper.class);

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper mapper;

    @InjectMocks
    private CategoryServiceImpl service;

    private Category category = null;

    @BeforeEach
    public void setup() {
        Optional<Category> category = Optional.of(createCategory());
        when(categoryRepository.findById(any(UUID.class)))
                .thenReturn(category);

        when(mapper.toResponse(any(Category.class)))
                .thenReturn(mapperHelper.toResponse(createCategory()));
    }

    @Test
    public void shouldThrowWhenNotFound() {
        // Arrange
        GetCategoryRequest request = createRequest();
        when(categoryRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {service.getCategory(request); } );
    }


    @Test
    public void shouldReturnMappedCategory() {
        // Arrange
        GetCategoryRequest request = createRequest();
        CategoryResponse expected = mapperHelper.toResponse(createCategory());

        // Act
        var response = service.getCategory(request);
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


    public GetCategoryRequest createRequest() {
        return GetCategoryRequest.builder()
                .id(UUID.randomUUID())
                .build();
    }
}
