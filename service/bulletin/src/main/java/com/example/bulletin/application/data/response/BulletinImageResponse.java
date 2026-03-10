package com.example.bulletin.application.data.response;

import lombok.Builder;
import lombok.Value;

import java.util.Objects;
import java.util.UUID;

@Value
@Builder
public class BulletinImageResponse {

    private UUID id;
    private UUID bulletinId;
    private UUID imageId;
    private boolean main;

    public boolean equalsData(BulletinImageResponse other) {
        if (other == null) return false;
        return Objects.equals(bulletinId, other.bulletinId) &&
                Objects.equals(imageId, other.imageId) &&
                Objects.equals(main, other.main);
    }

}
