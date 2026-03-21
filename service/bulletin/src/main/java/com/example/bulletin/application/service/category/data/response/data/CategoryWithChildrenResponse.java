package com.example.bulletin.application.service.category.data.response.data;

import com.example.bulletin.application.data.response.CategoryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryWithChildrenResponse {
    private UUID id;
    private String name;
    private boolean leaf;
    private UUID parentId;
    private List<CategoryResponse> children;
}
