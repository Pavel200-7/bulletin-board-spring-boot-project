package com.example.bulletin.domain.entity.base;

import com.example.bulletin.domain.entity.base.User.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Embeddable
public class OwnerInfo {

    @Column(name = "owner_id", nullable = false, insertable = false, updatable = false)
    private UUID ownerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    public UUID getOwnerId() {
        return ownerId;
    }

    public User getOwner() {
        return owner;
    }

    public boolean isOwnedByUserId(UUID userId) {
        return this.ownerId != null && this.ownerId.equals(userId);
    }

    public boolean isOwnedByUser(User user) {
        return this.ownerId != null && user != null && this.ownerId.equals(user.getId());
    }

    public boolean hasOwner() {
        return ownerId != null;
    }

}
