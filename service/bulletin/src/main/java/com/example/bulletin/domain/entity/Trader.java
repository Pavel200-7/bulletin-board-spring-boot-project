package com.example.bulletin.domain.entity;

import com.example.bulletin.domain.entity.base.BaseEntity;
import com.example.bulletin.domain.entity.base.Ownerable;
import com.example.bulletin.domain.entity.base.User.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class Trader extends BaseEntity implements Ownerable {

    @Column(name = "phone")
    private String phone;

    @Column(name = "contacts")
    private String contacts;

    @Column(name = "latitude", precision = 10, scale = 8)
    private Double latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private Double longitude;

    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private UUID ownerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    @Override
    public UUID getOwnerId() {
        return ownerId;
    }

    @Override
    public User getOwner() {
        return owner;
    }

}
