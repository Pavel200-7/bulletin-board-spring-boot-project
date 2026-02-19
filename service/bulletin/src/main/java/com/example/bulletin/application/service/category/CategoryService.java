package com.example.bulletin.application.service.category;

import com.example.bulletin.application.service.category.data.request.*;
import com.example.bulletin.application.service.category.data.response.*;

public interface CategoryService {
    public GetCategoryResponse getCategory(GetCategoryRequest request);
    public GetCategoryWithFamilyResponse getCategoryWithFamily(GetCategoryWithFamilyRequest request);
    public GetRootCategoriesResponse getRootCategories(GetRootCategoriesRequest request);
    public CreateRootCategoryResponse createRoot(CreateRootCategoryRequest request);
    public CreateChildCategoryResponse createChild(CreateChildCategoryRequest request);
    public CreateLeafyChildCategoryResponse createLeafyChild(CreateLeafyChildCategoryRequest request);
    public RenameCategoryResponse renameCategory(RenameCategoryRequest request);
    public DeleteCategoryResponse deleteCategory(DeleteCategoryRequest request);
    public DeleteLeafCategoryResponse deleteLeafCategory(DeleteLeafCategoryRequest request);
}
