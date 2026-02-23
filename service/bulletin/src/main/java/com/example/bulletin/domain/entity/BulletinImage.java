package com.example.bulletin.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Table(name = "bulletin_images")
public class BulletinImage {
    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bulletin_id", nullable = false)
    private Bulletin bulletin;

    @Column(name = "image_id", nullable = false)
    private UUID imageId;

    @Column(name = "main")
    private boolean main;

    protected BulletinImage() {}

    private BulletinImage(Bulletin bulletin, UUID imageId) {
        this.id = UUID.randomUUID();
        this.bulletin = bulletin;
        this.imageId = imageId;
        this.main = false;
    }

    public static BulletinImage createBulletinImage(Bulletin bulletin, UUID imageId){
        return new BulletinImage(bulletin, imageId);
    }

    public BulletinImage setMain() {
        this.main = true;
        return this;
    }

    public BulletinImage unsetMain() {
        this.main = false;
        return this;
    }
}

