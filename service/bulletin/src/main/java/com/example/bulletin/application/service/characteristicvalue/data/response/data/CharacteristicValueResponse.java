package com.example.bulletin.application.service.characteristicvalue.data.response.data;

import lombok.Builder;
import lombok.Value;

import java.util.Objects;
import java.util.UUID;

@Value
@Builder
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
