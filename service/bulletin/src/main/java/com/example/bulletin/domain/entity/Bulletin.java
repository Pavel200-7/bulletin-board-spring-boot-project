package com.example.bulletin.domain.entity;

import com.example.bulletin.domain.entity.base.BaseEntity;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Delegate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import com.example.bulletin.application.exception.AccessDeniedException;

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

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private BulletinState state;

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
        this.ownerInfo = ownerInfo;
        this.state = BulletinState.CREATED;
    }

    public static Bulletin createDraft(OwnerInfo ownerInfo) {
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
        validateForAddCharacteristic(characteristic);
        BulletinCharacteristic bulletinCharacteristic = BulletinCharacteristic.createBulletinCharacteristic(this, characteristic);
        this.characteristics.add(bulletinCharacteristic);
        return bulletinCharacteristic;
    }

    private void validateForAddCharacteristic(Characteristic characteristic) {
        if (this.category == null) {
            throw new IllegalStateException("Category should be set before characteristics.");
        }

        if (!characteristic.getCategory()
                .getId().equals(this.category.getId())) {
            throw new IllegalStateException("This characteristics is not of chosen category.");
        }

        if (isCharacteristicAlreadyExists(characteristic)) {
            throw new IllegalStateException("This characteristics is not unique for this bulletin.");
        }
    }

    private boolean isCharacteristicAlreadyExists(Characteristic characteristic) {
        return this.characteristics.stream()
                .anyMatch(bc -> bc.getName()
                        .getId().equals(characteristic.getId()));
    }

    public Bulletin removeCharacteristic(UUID removingId) {
        BulletinCharacteristic bulletinCharacteristic = findCharacteristic(removingId)
                .orElseThrow(() ->  new IllegalStateException("This characteristic is not present in this bulletin."));
        bulletinCharacteristic.delete();
        return this;
    }

    void removeCharacteristic(BulletinCharacteristic bulletinCharacteristic) {
        if (bulletinCharacteristic.getBulletin() != this) {
            throw new IllegalStateException("This characteristic is not present in this bulletin.");
        }
        this.characteristics.remove(bulletinCharacteristic);
    }

    private Optional<BulletinCharacteristic> findCharacteristic(UUID id) {
        return this.characteristics.stream()
                .filter(bc -> bc.getId()
                        .equals(id))
                .findFirst();
    }

    public BulletinCharacteristic setCharacteristicValue(CharacteristicValue value) {
        Characteristic characteristicValueOwned = value.getCharacteristic();
        var bulletinCharacteristic = findCharacteristicByNameId(characteristicValueOwned.getId())
                .orElseThrow(() -> new IllegalStateException("There is no characteristic this value owned."));
        return bulletinCharacteristic.setValue(value);
    }

    private Optional<BulletinCharacteristic> findCharacteristicByNameId(UUID id) {
        return this.characteristics.stream()
                .filter(bc -> bc.getName()
                        .getId().equals(id))
                .findFirst();
    }


    public BulletinImage addImage(UUID imageId) {
        BulletinImage image = BulletinImage.createBulletinImage(this, imageId);
        this.images.add(image);
        return image;
    }

    public BulletinImage setMainImage(UUID imageId) {
        BulletinImage image = findImage(imageId)
                .orElseThrow(() -> new IllegalStateException("Image not found in bulletin"));

        image.setMain();
        images.stream()
                .filter(i -> !i.getId().equals(imageId))
                .forEach(i -> i.unsetMain());
        return image;
    }

    public Bulletin removeImage(UUID removingId) {
        BulletinImage image = findImage(removingId)
                .orElseThrow(() -> new IllegalStateException("Image not found in bulletin"));
        image.delete();
        return this;
    }

    void removeImage(BulletinImage bulletinImage) {
        if (bulletinImage.getBulletin() != this) {
            throw new IllegalStateException("Image not found in bulletin");
        }
        this.images.remove(bulletinImage);
    }

    private Optional<BulletinImage> findImage(UUID imageId) {
        return this.images.stream()
                .filter(i -> i.getId().equals(imageId))
                .findFirst();
    }

    public boolean isActive() {
        return this.state
                .equals(BulletinState.PUBLISHED);
    }

}
