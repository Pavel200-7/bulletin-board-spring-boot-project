package com.example.bulletin.domain.vo;


import lombok.Builder;
import lombok.Value;

import java.util.Objects;
import java.util.UUID;

@Value
@Builder
public class CategoryData {
    private UUID id;
    private String name;
    private boolean leaf;
    private UUID parentId;

    public boolean equalsData(CategoryData other) {
        if (other == null) return false;
        return Objects.equals(name, other.name) &&
                leaf == other.leaf &&
                Objects.equals(parentId, other.parentId);
    }
}

