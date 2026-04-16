package com.example.bulletin.integration.hosts;

import com.example.bulletin.application.data.response.CategoryResponse;
import com.example.bulletin.application.service.category.data.request.CreateRootCategoryRequest;
import com.example.bulletin.application.service.category.data.response.*;
import com.example.bulletin.application.service.category.data.response.data.CategoryFamilyResponse;
import com.example.bulletin.config.TestConfig;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.infrastructure.repository.CategoryRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.given;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
@ActiveProfiles("test")
public class CategoryAPITests {

    @LocalServerPort
    protected int port;

    @Autowired
    private CategoryRepository repository;

    @Container
    protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    protected void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        repository.deleteAll();

    }



    @Test
    void ShouldGetCategoryById() {
        // Arrange
        Category category = Category.createRoot("root");
        repository.save(category);
        UUID categoryId = category.getId();

        // Act
        GetCategoryResponse response = given()
                .contentType(ContentType.JSON)
                .pathParam("id", categoryId)
                .when()
                    .get("/api/v1/category/{id}")
                .then()
                    .statusCode(HttpStatus.SC_OK)
                    .extract()
                    .as(GetCategoryResponse.class);

        // Assert
        assertNotNull(response);

        CategoryResponse categoryResponse = response.getCategoryResponse();
        assertNotNull(categoryResponse);
        assertEquals(category.getName(), categoryResponse.getName());
        assertEquals(category.isLeaf(), categoryResponse.isLeaf());
    }

    @Test
    void ShouldCreateRootCategory() {
        // Arrange
        CreateRootCategoryRequest request = CreateRootCategoryRequest.builder()
                .name("My first root")
                .build();

        // Act
        CreateRootCategoryResponse response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                    .post("/api/v1/category/root")
                .then()
                    .statusCode(HttpStatus.SC_OK)
                    .extract()
                    .as(CreateRootCategoryResponse.class);

        // Assert
        assertNotNull(response);

        CategoryResponse categoryResponse = response.getCategoryResponse();
        assertNotNull(categoryResponse);
        assertNotNull(categoryResponse.getId());
        assertEquals(request.getName(), categoryResponse.getName());
    }

    @Test
    void ShouldGetRootCategories() {
        // Arrange
        Category category1 = Category.createRoot("root 1");
        Category category2 = Category.createRoot("root 2");
        Category category3 = Category.createRoot("root 3");
        repository.save(category1);
        repository.save(category2);
        repository.save(category3);

        // Act
        GetRootCategoriesResponse response = given()
                .contentType(ContentType.JSON)
                .when()
                    .get("/api/v1/category/root")
                .then()
                    .statusCode(HttpStatus.SC_OK)
                    .extract()
                    .as(GetRootCategoriesResponse.class);

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
    void ShouldGetCategoryFamily() {
        // Arrange
        Category root = Category.createRoot("root 1");
        repository.save(root);

        Category firLvlChild = root.createChild("first level child");
        repository.save(firLvlChild);

        Category secLvlChild1 = firLvlChild.createLeafyChild("second level child 1");
        Category secLvlChild2 = firLvlChild.createLeafyChild("second level child ");
        Category secLvlChild3 = firLvlChild.createLeafyChild("second level child");
        repository.save(secLvlChild1);
        repository.save(secLvlChild2);
        repository.save(secLvlChild3);

        UUID categoryId = firLvlChild.getId();

        // Act
        GetCategoryWithFamilyResponse response = given()
                .contentType(ContentType.JSON)
                .pathParam("id", categoryId)
                .when()
                    .get("/api/v1/category/family/{id}")
                .then()
                    .statusCode(HttpStatus.SC_OK)
                    .extract()
                    .as(GetCategoryWithFamilyResponse.class);

        // Assert
        assertNotNull(response);

        CategoryFamilyResponse categoryResponseRoot = response.getCategoryFamilyResponse();
        assertEquals(root.getName(), categoryResponseRoot.getName());

        CategoryFamilyResponse categoryResponseFirLvlChild = categoryResponseRoot.getChildren().getFirst();
        assertEquals(firLvlChild.getName(), categoryResponseFirLvlChild.getName());
        assertEquals(3, categoryResponseFirLvlChild.getChildren().size());
    }

}
