package com.example.bulletin.domain.vo;

import lombok.Builder;
import lombok.Value;

import java.util.Objects;
import java.util.UUID;

@Value
@Builder
public class BulletinImageData {
    private UUID id;
    private UUID bulletinId;
    private UUID imageId;
    private boolean main;

    public boolean equalsData(BulletinImageData other) {
        if (other == null) return false;
        return Objects.equals(bulletinId, other.bulletinId) &&
                Objects.equals(imageId, other.imageId) &&
                Objects.equals(main, other.main);
    }

}
