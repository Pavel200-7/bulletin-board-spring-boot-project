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
public class CharacteristicValueResponse {
    private UUID id;
    private String name;
    private UUID characteristicId;

    public boolean equalsData(CharacteristicValueResponse other) {
        if (other == null) return false;
        return Objects.equals(name, other.name) &&
                Objects.equals(characteristicId, other.characteristicId);
    }
}
