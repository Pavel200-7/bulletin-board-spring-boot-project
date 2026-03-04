package com.example.bulletin.unit.application.service.category.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.CategoryMapper;
import com.example.bulletin.application.service.category.CategoryServiceImpl;
import com.example.bulletin.application.service.category.data.request.GetCategoryWithFamilyRequest;
import com.example.bulletin.application.service.category.data.response.data.CategoryFamilyResponse;
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
public class GetCategoryWithFamilyTests {

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


    private Category category = null;

    @BeforeEach
    public void setup() {
        Optional<Category> category = Optional.of(createCategory());
        when(categoryRepository.findById(any(UUID.class)))
                .thenReturn(category);

        when(responseBuilder.buildResponse(any(Category.class)))
                .thenReturn(createCategoryFamilyResponse());
    }

    @Test
    public void shouldThrowWhenNotFound() {
        // Arrange
        GetCategoryWithFamilyRequest request = createRequest();
        when(categoryRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {service.getCategoryWithFamily(request); } );
    }


    @Test
    public void shouldReturnCategoryWithFamilyResponse() {
        // Arrange
        GetCategoryWithFamilyRequest request = createRequest();
        CategoryFamilyResponse expected = createCategoryFamilyResponse();

        // Act
        var response = service.getCategoryWithFamily(request);
        CategoryFamilyResponse actual = response.getCategoryFamilyResponse();

        // Assert
        assertTrue(expected.equalsData(actual));
    }

    public Category createCategory() {
        if (category == null) {
            category = Category.createRoot("root");
        }
        return category;
    }

    public GetCategoryWithFamilyRequest createRequest() {
        return GetCategoryWithFamilyRequest.builder()
                .id(UUID.randomUUID())
                .build();
    }

    public CategoryFamilyResponse createCategoryFamilyResponse() {
        return CategoryFamilyResponse.builder()
                .id(UUID.randomUUID())
                .name("some name")
                .leaf(false)
                .build();
    }

}
