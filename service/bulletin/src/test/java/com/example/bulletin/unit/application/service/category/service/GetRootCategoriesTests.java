package com.example.bulletin.unit.application.service.category.service;

import com.example.bulletin.application.mapper.CategoryMapper;
import com.example.bulletin.application.service.category.CategoryServiceImpl;
import com.example.bulletin.application.service.category.data.request.GetRootCategoriesRequest;
import com.example.bulletin.application.service.category.data.response.GetRootCategoriesResponse;
import com.example.bulletin.application.data.response.CategoryResponse;
import com.example.bulletin.application.service.category.helper.inter.CategoryFamilyResponseBuilder;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.infrastructure.repository.BulletinRepository;
import com.example.bulletin.infrastructure.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GetRootCategoriesTests {

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

    private List<Category> categories = null;

    @BeforeEach
    public void setup() {
        when(categoryRepository.findByParentId(null))
                .thenReturn(createCategories());

        when(mapper.toResponse(any(Category.class)))
                .thenAnswer(invocation -> {
                    Category categoryToMap = invocation.getArgument(0);
                    return mapperHelper.toResponse(categoryToMap);
                });
    }

    @Test
    public void shouldReadRootCategories() {
        // Arrange
        GetRootCategoriesRequest request = createRequest();

        // Act
        service.getRootCategories(request);

        // Assert
        verify(categoryRepository, Mockito.times(1)).findByParentId(null);
    }

    @Test
    public void shouldMapCategories() {
        // Arrange
        GetRootCategoriesRequest request = createRequest();
        List<CategoryResponse> expected = createCategories().stream()
                .map(c -> mapperHelper.toResponse(c))
                .collect(Collectors.toList());

        // Act
        GetRootCategoriesResponse response = service.getRootCategories(request);
        List<CategoryResponse> actual = response.getCategoryResponse();

        // Assert
        assertThat(expected)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(actual);
    }

    public GetRootCategoriesRequest createRequest() {
        return GetRootCategoriesRequest.builder()
                .build();
    }

    public List<Category> createCategories() {
        if (categories == null) {
            categories = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                String rootName = "root ".concat(String.valueOf(i));
                categories.add(Category.createRoot(rootName));
            }
        }
        return categories;
    }
}
