package com.example.bulletin.application.data.response;

import com.example.bulletin.domain.enums.bulletin.BulletinState;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;

@Value
@Builder
public class BulletinResponse {
    private UUID id;
    private UUID ownerId;
    private String title;
    private String description;
    private double price;
    private double rating;
    private BulletinState state;
    private CategoryResponse category;
    private List<BulletinCharacteristicResponse> characteristics;
    private List<BulletinImageResponse> images;

    public boolean equalsData(BulletinResponse other) {
        if (other == null) return false;
        return Objects.equals(ownerId, other.ownerId) &&
                Objects.equals(title, other.title) &&
                Objects.equals(description, other.description) &&
                Objects.equals(price, other.price) &&
                Objects.equals(rating, other.rating) &&
                Objects.equals(state, other.state) &&
                (category == null && other.category == null || category.equalsData(other.category)) &&
                isCharacteristicsEqual(other) &&
                isImagesEqual(other);
    }

    private boolean isCharacteristicsEqual(BulletinResponse other) {
        if (characteristics == null && other.characteristics == null) return true;
        if (characteristics == null || other.characteristics == null) return false;
        if (characteristics.size() != other.characteristics.size()) return false;

        return IntStream.range(0, characteristics.size())
                .allMatch(i -> characteristics.get(i).equalsData(
                        other.characteristics.get(i))
                );
    }

    private boolean isImagesEqual(BulletinResponse other) {
        if (images == null && other.images == null) return true;
        if (images == null || other.images == null) return false;
        if (images.size() != other.images.size()) return false;

        return IntStream.range(0, images.size())
                .allMatch(i -> images.get(i).equalsData(
                        other.images.get(i))
                );
    }

}
