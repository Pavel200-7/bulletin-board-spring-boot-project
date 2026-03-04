package com.example.bulletin.application.data.response;

import lombok.Builder;
import lombok.Value;

import java.util.Objects;
import java.util.UUID;

@Value
@Builder
public class BulletinCharacteristicResponse {
    private UUID id;
    private UUID bulletinId;
    private CharacteristicResponse name;
    private CharacteristicValueResponse value;

    public boolean equalsData(BulletinCharacteristicResponse other) {
        if (other == null) return false;
        return Objects.equals(bulletinId, other.bulletinId) &&
                (name == null && other.name == null || name.equalsData(other.name)) &&
                (value == null && other.value == null || value.equalsData(other.value));
    }

}
