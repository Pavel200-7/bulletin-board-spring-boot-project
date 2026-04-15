package com.example.bulletin.integration.hosts;

import com.example.bulletin.application.data.response.CategoryResponse;
import com.example.bulletin.application.service.category.data.response.*;
import com.example.bulletin.config.TestConfig;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.infrastructure.repository.CategoryRepository;
import com.example.bulletin.integration.hosts.base.BaseAPITests;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.given;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
@ActiveProfiles("test")
@Slf4j
public class CategoryAPITests {

    @LocalServerPort
    private int port;

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
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Autowired
    private CategoryRepository repository;

//    @Override
//    protected void setUp() {
//        super.setUp();
////        repository.deleteAll();
//    }

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

}
