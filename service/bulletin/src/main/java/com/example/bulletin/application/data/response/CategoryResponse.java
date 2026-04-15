package com.example.bulletin.application.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Objects;
import java.util.UUID;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class CategoryResponse {
    private UUID id;
    private String name;
    private boolean leaf;
    private UUID parentId;

    public boolean equalsData(CategoryResponse other) {
        if (other == null) return false;
        return Objects.equals(name, other.name) &&
                leaf == other.leaf &&
                Objects.equals(parentId, other.parentId);
    }
}
