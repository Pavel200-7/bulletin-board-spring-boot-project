package com.example.bulletin.host.controller;

import com.example.bulletin.application.service.category.CategoryService;
import com.example.bulletin.application.service.category.data.request.*;
import com.example.bulletin.application.service.category.data.response.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryController {

    private final CategoryService service;

    @PostMapping("/root")
    public ResponseEntity<CreateRootCategoryResponse> createRoot(@Valid @RequestBody CreateRootCategoryRequest request) {
        return ResponseEntity.ok(service.createRoot(request));
    }

    @PostMapping("/child")
    public ResponseEntity<CreateChildCategoryResponse> createChild(@Valid @RequestBody CreateChildCategoryRequest request) {
        return ResponseEntity.ok(service.createChild(request));
    }

    @PostMapping("/leafy_child")
    public ResponseEntity<CreateLeafyChildCategoryResponse> createLeafyChild(@Valid @RequestBody CreateLeafyChildCategoryRequest request) {
        return ResponseEntity.ok(service.createLeafyChild(request));
    }

    @PutMapping("/name")
    public ResponseEntity<RenameCategoryResponse> renameCategory(@Valid @RequestBody RenameCategoryRequest request) {
        return ResponseEntity.ok(service.renameCategory(request));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCategory(@Valid @RequestBody DeleteCategoryRequest request) {
        service.deleteCategory(request);
        return ResponseEntity.noContent().build();
    }

}
