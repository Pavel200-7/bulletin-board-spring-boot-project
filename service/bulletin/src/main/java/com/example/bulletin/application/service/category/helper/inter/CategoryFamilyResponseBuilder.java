package com.example.bulletin.application.service.category.helper.inter;

import com.example.bulletin.application.service.category.data.response.data.CategoryFamilyResponse;
import com.example.bulletin.domain.entity.Category;

public interface CategoryFamilyResponseBuilder {
    public CategoryFamilyResponse buildResponse(Category category);
}
