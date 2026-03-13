package com.example.chat.domain.entity;

import com.example.chat.domain.entity.base.BaseEntity;
import com.example.chat.domain.entity.base.OwnerInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "profile")
public class Profile extends BaseEntity {

    @Id
    @Column(name = "id")
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Embedded
    @Delegate
    @Setter(AccessLevel.NONE)
    private OwnerInfo ownerInfo;

    @Column(name = "public_name")
    private String publicName;

    @Column(name = "description")
    private String description;

    @Column(name = "image_id")
    private UUID imageId;

    @OneToMany(mappedBy = "ownerProfile",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Contact> contacts;

    protected Profile() {};

    private Profile(OwnerInfo ownerInfo, String publicName) {
        this.id = UUID.randomUUID();
        this.ownerInfo = ownerInfo;
        this.publicName = publicName;
    }

    public static Profile createProfile(OwnerInfo ownerInfo, String publicName) { return new Profile(ownerInfo, publicName); }

}
