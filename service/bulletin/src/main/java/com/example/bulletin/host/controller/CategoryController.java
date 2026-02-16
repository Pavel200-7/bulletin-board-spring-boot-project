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

    @GetMapping("/{categoryId}")
    public ResponseEntity<GetCategoryResponse> getCategory(@PathVariable UUID categoryId) {
        GetCategoryRequest request = new GetCategoryRequest(categoryId);
        return ResponseEntity.ok(service.getCategory(request));
    }

    @GetMapping("/family/{categoryId}")
    public ResponseEntity<GetCategoryWithFamilyResponse> getCategoryWithFamily(@PathVariable UUID categoryId) {
        GetCategoryWithFamilyRequest request = new GetCategoryWithFamilyRequest(categoryId);
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

    @PostMapping("/leafy_child")
    public ResponseEntity<CreateLeafyChildCategoryResponse> createLeafyChild(@Valid @RequestBody CreateLeafyChildCategoryRequest request) {
        return ResponseEntity.ok(service.createLeafyChild(request));
    }

    @PutMapping("/name")
    public ResponseEntity<RenameCategoryResponse> renameCategory(@Valid @RequestBody RenameCategoryRequest request) {
        return ResponseEntity.ok(service.renameCategory(request));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID categoryId) {
        service.deleteCategory(new DeleteCategoryRequest(categoryId));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/leaf/{categoryId}")
    public ResponseEntity<Void> deleteLeafCategory(@PathVariable UUID categoryId) {
        service.deleteLeafCategory(new DeleteLeafCategoryRequest(categoryId));
        return ResponseEntity.noContent().build();
    }

}
