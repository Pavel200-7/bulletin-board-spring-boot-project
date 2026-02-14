package com.example.bulletin.application.service.category;

import com.example.bulletin.application.service.category.data.request.CreateRootCategoryRequest;
import com.example.bulletin.application.service.category.data.response.CreateRootCategoryResponse;

public interface CategoryService {
    public CreateRootCategoryResponse createRoot(CreateRootCategoryRequest request);
}
