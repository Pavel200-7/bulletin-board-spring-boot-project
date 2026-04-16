package com.example.bulletin.integration.hosts.helper.client;

import com.example.bulletin.application.service.bulletin.data.request.UpdateBulletinRequest;
import com.example.bulletin.application.service.bulletin.data.response.CreateBulletinResponse;
import com.example.bulletin.application.service.bulletin.data.response.UpdateBulletinResponse;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;

import static io.restassured.RestAssured.given;

public class BulletinAPIClient extends BaseAPIClient {

    public BulletinAPIClient(int port) {
        super(port);
    }

    public CreateBulletinResponse createDraft() {
        return given()
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/bulletin")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(CreateBulletinResponse.class);
    }

    public UpdateBulletinResponse updateBulletin(UpdateBulletinRequest request) {
        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/api/v1/bulletin")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(UpdateBulletinResponse.class);
    }

}
