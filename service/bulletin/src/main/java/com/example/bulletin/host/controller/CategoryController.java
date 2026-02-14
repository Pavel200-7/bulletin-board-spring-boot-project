package com.example.bulletin.host.controller;

import com.example.bulletin.application.service.category.CategoryService;
import com.example.bulletin.application.service.category.data.request.CreateRootCategoryRequest;
import com.example.bulletin.application.service.category.data.response.CreateRootCategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryController {

    private final CategoryService service;

    @PostMapping("/root")
    public ResponseEntity<CreateRootCategoryResponse> createRoot(@Valid @RequestBody CreateRootCategoryRequest request) {
        return ResponseEntity.ok(service.createRoot(request));
    }

}
