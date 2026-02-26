package com.example.bulletin.application.service.category;

import com.example.bulletin.application.service.category.data.request.*;
import com.example.bulletin.application.service.category.data.response.*;

public interface CategoryService {
    GetCategoryResponse getCategory(GetCategoryRequest request);
    GetCategoryWithFamilyResponse getCategoryWithFamily(GetCategoryWithFamilyRequest request);
    GetRootCategoriesResponse getRootCategories(GetRootCategoriesRequest request);
    CreateRootCategoryResponse createRoot(CreateRootCategoryRequest request);
    CreateChildCategoryResponse createChild(CreateChildCategoryRequest request);
    CreateLeafyChildCategoryResponse createLeafyChild(CreateLeafyChildCategoryRequest request);
    RenameCategoryResponse renameCategory(RenameCategoryRequest request);
    DeleteChildCategoryResponse deleteChildCategory(DeleteChildCategoryRequest request);
    DeleteRootCategoryResponse deleteRootCategory(DeleteRootCategoryRequest request);
}
