package com.example.bulletin.integration.hosts.helper.client;

import io.restassured.RestAssured;

public abstract class BaseAPIClient {
    
    public BaseAPIClient(int port) {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }
}
