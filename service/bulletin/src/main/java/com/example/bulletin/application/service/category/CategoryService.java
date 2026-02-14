package com.example.bulletin.application.service.category;

import com.example.bulletin.application.service.category.data.request.*;
import com.example.bulletin.application.service.category.data.response.*;

public interface CategoryService {
    public CreateRootCategoryResponse createRoot(CreateRootCategoryRequest request);
    public CreateChildCategoryResponse createChild(CreateChildCategoryRequest request);
    public CreateLeafyChildCategoryResponse createLeafyChild(CreateLeafyChildCategoryRequest request);
}
