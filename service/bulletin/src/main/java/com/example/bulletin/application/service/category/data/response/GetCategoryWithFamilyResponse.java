package com.example.bulletin.application.service.category.data.response;

import com.example.bulletin.application.service.category.data.response.data.CategoryFamilyResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCategoryWithFamilyResponse {
    private CategoryFamilyResponse categoryFamilyResponse;
}
