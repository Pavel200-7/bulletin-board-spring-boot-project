package com.example.bulletin.domain.entity;

import com.example.bulletin.domain.entity.base.BaseEntity;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.enums.BulletinStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Delegate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "bulletins")
public class Bulletin extends BaseEntity {

    @Id
    @Column(name = "id")
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Embedded
    @Delegate
    @Setter(AccessLevel.NONE)
    private OwnerInfo ownerInfo;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price")
    private double price;

    @Column(name = "rating")
    private double rating;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    @Setter(AccessLevel.NONE)
    private BulletinStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @Setter(AccessLevel.NONE)
    private Category category;

    @OneToMany(mappedBy = "bulletin",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<BulletinCharacteristic> characteristics = new ArrayList<>();

    @OneToMany(mappedBy = "bulletin",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<BulletinImage> images = new ArrayList<>();

    protected Bulletin() {}

    private Bulletin(OwnerInfo ownerInfo) {
        this.id = UUID.randomUUID();
        this.status = BulletinStatus.DRAFT;
        this.ownerInfo = ownerInfo;
    }

    public static Bulletin createDraft(OwnerInfo ownerInfo)
            throws AccessDeniedException {
        if (ownerInfo.getOwner().isBlocked()) {
            throw new AccessDeniedException("Your account is blocked.");
        }
        return new Bulletin(ownerInfo);
    }

    public Bulletin setCategory(Category category) {
        if (!category.isLeaf()) {
            throw new IllegalStateException("Cannot set not leaf category.");
        }
        this.characteristics.clear();
        this.category = category;
        return this;
    }

    public BulletinCharacteristic addCharacteristic(Characteristic characteristic) {
        if (this.category == null) {
            throw new IllegalStateException("Category should be set before characteristics.");
        }

        if (!characteristic.getCategory().equals(this.category)) {
            throw new IllegalStateException("This characteristics is not of chosen category.");
        }

        if (isCharacteristicAlreadyExists(characteristic)) {
            throw new IllegalStateException("This characteristics is not unique for this bulletin.");
        }

        BulletinCharacteristic bulletinCharacteristic = BulletinCharacteristic.createBulletinCharacteristic(this, characteristic);
        this.characteristics.add(bulletinCharacteristic);
        return bulletinCharacteristic;
    }

    private boolean isCharacteristicAlreadyExists(Characteristic characteristic) {
        return this.characteristics.stream()
                .anyMatch(bc -> bc.getName().getId().equals(characteristic.getId()));
    }

    public void removeCharacteristic(BulletinCharacteristic characteristic) {
        if (!this.characteristics.contains(characteristic)) {
            throw new IllegalStateException("This characteristic is not present in this bulletin.");
        }

        if (!characteristic.getBulletin().equals(this)) {
            throw new IllegalStateException("This characteristic belongs to another bulletin.");
        }

        this.characteristics.remove(characteristic);
    }

    public BulletinCharacteristic setCharacteristicValue(CharacteristicValue value) {
        var bulletinCharacteristic = findBulletinCharacteristicByCharacteristic(value.getCharacteristic())
                .orElseThrow(() -> new IllegalStateException("There is no characteristic this value owned."));
        return bulletinCharacteristic.setValue(value);
    }

    private Optional<BulletinCharacteristic> findBulletinCharacteristicByCharacteristic(Characteristic characteristic) {
        return this.characteristics.stream()
                .filter(c -> c.getName().getId()
                        .equals(characteristic.getId()))
                .findFirst();
    }

    public BulletinImage addImage(UUID imageId) {
        BulletinImage image = BulletinImage.createBulletinImage(this, imageId);
        this.images.add(image);
        return image;
    }

    public void removeImage(BulletinImage image) {
        BulletinImage existingImage = findImageById(image.getImageId())
                .orElseThrow(() -> new IllegalStateException("Image not found in bulletin"));

        this.images.remove(image);
    }

    private boolean isImageExists(BulletinImage image) {
        return this.images.stream()
                .anyMatch(bi -> bi.getImageId().equals(image.getImageId()));
    }

    public BulletinImage setMainImage(BulletinImage image) {
        BulletinImage existingImage = findImageById(image.getImageId())
                .orElseThrow(() -> new IllegalStateException("Image not found in bulletin"));

        image.setMain();
        images.stream()
                .filter(i -> !i.getImageId().equals(image.getImageId()))
                        .forEach(i -> i.unsetMain());
        return image;
    }

    private Optional<BulletinImage> findImageById(UUID imageId) {
        return this.images.stream()
                .filter(i -> i.getImageId().equals(imageId))
                .findFirst();
    }

}
