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

}
