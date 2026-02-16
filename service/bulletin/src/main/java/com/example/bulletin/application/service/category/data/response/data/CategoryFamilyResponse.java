package com.example.bulletin.application.service.category.data.response.data;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Value
@Builder
public class CategoryFamilyResponse {
    private UUID id;
    private String name;
    private boolean leaf;
    private UUID parentId;
    private List<CategoryFamilyResponse> children;

    public boolean equalsData(CategoryFamilyResponse other) {
        if (other == null) return false;
        return Objects.equals(name, other.name) &&
                leaf == other.leaf &&
                Objects.equals(parentId, other.parentId);
    }
}
