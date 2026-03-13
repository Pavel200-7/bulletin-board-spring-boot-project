package com.example.chat.domain.entity;

import com.example.chat.domain.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "contact")
public class Contact extends BaseEntity {

    @Id
    @Column(name = "id")
    @Setter(AccessLevel.NONE)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_profile_id")
    @Setter(AccessLevel.NONE)
    private Profile ownerProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_profile_id")
    @Setter(AccessLevel.NONE)
    private Profile contactProfile;

    @Column(name = "contact_name")
    private String contactName;

    protected Contact() {}

    private Contact(Profile ownerProfile, Profile contactProfile, String contactName) {
        this.id = UUID.randomUUID();
        this.ownerProfile = ownerProfile;
        this.contactProfile = contactProfile;
        this.contactName = contactName;
    }

    public static Contact createContact(Profile ownerProfile, Profile contactProfile) {
        return new Contact(ownerProfile, contactProfile, contactProfile.getPublicName());
    }

    public Contact changeContactName(String newContactName) {
        this.contactName = newContactName;
        return this;
    }

    void delete() {
        Profile owner = this.ownerProfile;
        if (owner != null) {
            owner.removeContact(this);
            this.ownerProfile = null;
        }
        this.contactProfile = null;
    }

}
