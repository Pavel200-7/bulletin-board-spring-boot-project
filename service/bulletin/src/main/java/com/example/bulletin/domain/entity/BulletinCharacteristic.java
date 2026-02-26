package com.example.bulletin.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Table(name = "bulletin_characteristics")
public class BulletinCharacteristic {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bulletin_id", nullable = false)
    private Bulletin bulletin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "characteristic_id", nullable = false)
    private Characteristic name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "value_id", nullable = false)
    private CharacteristicValue value;

    protected BulletinCharacteristic() {}

    private BulletinCharacteristic(Bulletin bulletin, Characteristic name) {
        this.id = UUID.randomUUID();
        this.bulletin = bulletin;
        this.name = name;
    }

    public static BulletinCharacteristic createBulletinCharacteristic(Bulletin bulletin, Characteristic name) {
        return new BulletinCharacteristic(bulletin, name);
    }

    public BulletinCharacteristic setValue(CharacteristicValue value) {
        if (!value.getCharacteristic()
                .getId().equals(this.name.getId())) {
            throw new IllegalStateException("This is not characteristic value of existing characteristic.");
        }
        this.value = value;
        return this;
    }

    void delete() {
        Bulletin owner = this.bulletin;
        if (owner != null) {
            owner.removeCharacteristic(this);
            this.bulletin = null;
        }
    }

}
