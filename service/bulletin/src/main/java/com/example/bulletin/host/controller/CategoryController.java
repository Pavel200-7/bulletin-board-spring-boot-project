package com.example.bulletin.host.controller;

import com.example.bulletin.application.service.category.CategoryService;
import com.example.bulletin.application.service.category.data.request.*;
import com.example.bulletin.application.service.category.data.response.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryController {

    private final CategoryService service;

    @GetMapping("/{id}")
    public ResponseEntity<GetCategoryResponse> getCategory(@PathVariable UUID id) {
        GetCategoryRequest request = new GetCategoryRequest(id);
        return ResponseEntity.ok(service.getCategory(request));
    }

    @GetMapping("/root")
    public ResponseEntity<GetRootCategoriesResponse> getRootCategories() {
        GetRootCategoriesRequest request = new GetRootCategoriesRequest();
        return ResponseEntity.ok(service.getRootCategories(request));
    }

    @GetMapping("/family/{id}")
    public ResponseEntity<GetCategoryWithFamilyResponse> getCategoryWithFamily(@PathVariable UUID id) {
        GetCategoryWithFamilyRequest request = new GetCategoryWithFamilyRequest(id);
        return ResponseEntity.ok(service.getCategoryWithFamily(request));
    }

    @PostMapping("/root")
    public ResponseEntity<CreateRootCategoryResponse> createRoot(@Valid @RequestBody CreateRootCategoryRequest request) {
        return ResponseEntity.ok(service.createRoot(request));
    }

    @PostMapping("/child")
    public ResponseEntity<CreateChildCategoryResponse> createChild(@Valid @RequestBody CreateChildCategoryRequest request) {
        return ResponseEntity.ok(service.createChild(request));
    }

    @PostMapping("/leafy-child")
    public ResponseEntity<CreateLeafyChildCategoryResponse> createLeafyChild(@Valid @RequestBody CreateLeafyChildCategoryRequest request) {
        return ResponseEntity.ok(service.createLeafyChild(request));
    }

    @PutMapping("/name")
    public ResponseEntity<RenameCategoryResponse> renameCategory(@Valid @RequestBody RenameCategoryRequest request) {
        return ResponseEntity.ok(service.renameCategory(request));
    }

    @DeleteMapping("/{parentId}/children/{childId}")
    public ResponseEntity<Void> deleteChildCategory(@PathVariable UUID parentId, @PathVariable UUID childId) {
        service.deleteChildCategory(new DeleteChildCategoryRequest(parentId, childId));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/root/{id}")
    public ResponseEntity<Void> deleteRootCategory(@PathVariable UUID id) {
        service.deleteRootCategory(new DeleteRootCategoryRequest(id));
        return ResponseEntity.noContent().build();
    }

}
