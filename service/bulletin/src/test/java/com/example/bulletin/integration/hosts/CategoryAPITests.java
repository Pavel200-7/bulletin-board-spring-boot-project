package com.example.bulletin.integration.hosts;

import com.example.bulletin.application.data.response.CategoryResponse;
import com.example.bulletin.application.service.category.data.request.CreateRootCategoryRequest;
import com.example.bulletin.application.service.category.data.response.*;
import com.example.bulletin.application.service.category.data.response.data.CategoryFamilyResponse;
import com.example.bulletin.config.TestConfig;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.integration.hosts.helper.cleaner.DatabaseCleaner;
import com.example.bulletin.integration.hosts.helper.client.CategoryAPIClient;
import com.example.bulletin.integration.hosts.helper.initializer.CategoryInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
@ActiveProfiles("test")
public class CategoryAPITests {

    @LocalServerPort
    protected int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private CategoryInitializer categoryInitializer;

    private CategoryAPIClient categoryAPIClient;


    @Container
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    private static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    protected void setUp() {
        categoryAPIClient = new CategoryAPIClient(port);
        databaseCleaner.cleanAll();
    }

    @Test
    public void ShouldGetCategoryById() {
        // Arrange
        Category category = categoryInitializer.createRoot("root");

        // Act
        GetCategoryResponse response = categoryAPIClient.getCategory(category.getId());

        // Assert
        assertNotNull(response);

        CategoryResponse categoryResponse = response.getCategoryResponse();
        assertNotNull(categoryResponse);
        assertEquals(category.getName(), categoryResponse.getName());
        assertEquals(category.isLeaf(), categoryResponse.isLeaf());
    }

    @Test
    public void ShouldCreateRootCategory() {
        // Arrange
        CreateRootCategoryRequest request = CreateRootCategoryRequest.builder()
                .name("My first root")
                .build();

        // Act
        CreateRootCategoryResponse response = categoryAPIClient.createRoot(request);

        // Assert
        assertNotNull(response);

        CategoryResponse categoryResponse = response.getCategoryResponse();
        assertNotNull(categoryResponse);
        assertNotNull(categoryResponse.getId());
        assertEquals(request.getName(), categoryResponse.getName());
    }

    @Test
    public void ShouldGetRootCategories() {
        // Arrange
        Category category1 = categoryInitializer.createRoot("root 1");
        Category category2 = categoryInitializer.createRoot("root 2");
        Category category3 = categoryInitializer.createRoot("root 3");

        // Act
        GetRootCategoriesResponse response = categoryAPIClient.getRoot();

        // Assert
        assertNotNull(response);

        List<CategoryResponse> categoryResponse = response.getCategoryResponse();
        assertFalse(categoryResponse.isEmpty());
        assertEquals(3, categoryResponse.size());
        assertTrue(categoryResponse.stream().anyMatch(c -> c.getName().equals(category1.getName())));
        assertTrue(categoryResponse.stream().anyMatch(c -> c.getName().equals(category2.getName())));
        assertTrue(categoryResponse.stream().anyMatch(c -> c.getName().equals(category3.getName())));
    }

    @Test
    public void ShouldGetCategoryFamily() {
        // Arrange
        Category category = categoryInitializer.createNotRootCategoryWithLeafyChildren();

        // Act
        GetCategoryWithFamilyResponse response = categoryAPIClient.getCategoryWithFamily(category.getId());

        // Assert
        assertNotNull(response);

        CategoryFamilyResponse categoryResponseRoot = response.getCategoryFamilyResponse();
        assertEquals(category.getParent().getName(), categoryResponseRoot.getName());

        CategoryFamilyResponse categoryResponseFirLvlChild = categoryResponseRoot.getChildren().getFirst();
        assertEquals(category.getName(), categoryResponseFirLvlChild.getName());
        assertEquals(3, categoryResponseFirLvlChild.getChildren().size());
    }

}
