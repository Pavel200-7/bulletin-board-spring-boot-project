package com.example.bulletin.domain.vo;

import lombok.Builder;
import lombok.Value;

import java.util.Objects;
import java.util.UUID;

@Value
@Builder
public class CharacteristicData {
    private UUID id;
    private String name;
    private UUID categoryId;

    public boolean equalsData(CharacteristicData other) {
        if (other == null) return false;
        return Objects.equals(name, other.name) &&
                Objects.equals(categoryId, other.categoryId);
    }
}
