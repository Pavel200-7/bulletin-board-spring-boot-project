package com.example.bulletin.domain.vo;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;
import java.util.UUID;

@Value
@Builder
public class BulletinCharacteristicData {
    private UUID id;
    private UUID bulletinId;
    private CharacteristicData name;
    private CharacteristicValueData value;

    public boolean equalsData(BulletinCharacteristicData other) {
        if (other == null) return false;
        return Objects.equals(bulletinId, other.bulletinId) &&
                (name == null && other.name == null || name.equalsData(other.name)) &&
                (value == null && other.value == null || value.equalsData(other.value));
    }

}
