package com.example.bulletin.application.service.category.data.response;

import com.example.bulletin.application.service.category.data.response.data.CategoryWithChildrenResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCategoryWithChildrenResponse {
    private CategoryWithChildrenResponse categoryWithChildrenResponse;
}
