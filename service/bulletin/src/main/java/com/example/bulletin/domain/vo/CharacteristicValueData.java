package com.example.bulletin.domain.vo;

import lombok.Builder;
import lombok.Value;

import java.util.Objects;
import java.util.UUID;

@Value
@Builder
public class CharacteristicValueData {
    private UUID id;
    private String name;
    private UUID characteristicId;

    public boolean equalsData(CharacteristicValueData other) {
        if (other == null) return false;
        return Objects.equals(name, other.name) &&
                Objects.equals(characteristicId, other.characteristicId);
    }

}
