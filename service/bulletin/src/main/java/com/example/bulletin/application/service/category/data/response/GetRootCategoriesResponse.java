package com.example.bulletin.application.service.category.data.response;

import com.example.bulletin.application.data.response.CategoryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetRootCategoriesResponse {
    private List<CategoryResponse> categoryResponse;
}
