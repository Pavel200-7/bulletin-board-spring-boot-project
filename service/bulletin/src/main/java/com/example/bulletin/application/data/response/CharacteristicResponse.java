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
public class CharacteristicResponse {
    private UUID id;
    private String name;
    private UUID categoryId;

    public boolean equalsData(CharacteristicResponse other) {
        if (other == null) return false;
        return Objects.equals(name, other.name) &&
                Objects.equals(categoryId, other.categoryId);
    }
}
