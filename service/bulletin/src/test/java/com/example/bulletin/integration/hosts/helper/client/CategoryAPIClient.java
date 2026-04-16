package com.example.bulletin.integration.hosts.helper.client;

import com.example.bulletin.application.service.category.data.request.CreateRootCategoryRequest;
import com.example.bulletin.application.service.category.data.request.GetCategoryWithFamilyRequest;
import com.example.bulletin.application.service.category.data.response.CreateRootCategoryResponse;
import com.example.bulletin.application.service.category.data.response.GetCategoryResponse;
import com.example.bulletin.application.service.category.data.response.GetCategoryWithFamilyResponse;
import com.example.bulletin.application.service.category.data.response.GetRootCategoriesResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;

import java.util.UUID;

import static io.restassured.RestAssured.given;

public class CategoryAPIClient extends BulletinAPIClient {

    public CategoryAPIClient(int port) {
        super(port);
    }

    public GetCategoryResponse getCategory(UUID id) {
        return given()
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .when()
                    .get("/api/v1/category/{id}")
                .then()
                    .statusCode(HttpStatus.SC_OK)
                    .extract()
                    .as(GetCategoryResponse.class);
    }

    public CreateRootCategoryResponse createRoot(CreateRootCategoryRequest request) {
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                    .post("/api/v1/category/root")
                .then()
                    .statusCode(HttpStatus.SC_OK)
                    .extract()
                    .as(CreateRootCategoryResponse.class);
    }

    public GetRootCategoriesResponse getRoot() {
        return given()
                .contentType(ContentType.JSON)
                .when()
                    .get("/api/v1/category/root")
                .then()
                    .statusCode(HttpStatus.SC_OK)
                    .extract()
                    .as(GetRootCategoriesResponse.class);
    }

    public GetCategoryWithFamilyResponse getCategoryWithFamily(UUID id) {
        return given()
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .when()
                .get("/api/v1/category/family/{id}")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(GetCategoryWithFamilyResponse.class);
    }
}
