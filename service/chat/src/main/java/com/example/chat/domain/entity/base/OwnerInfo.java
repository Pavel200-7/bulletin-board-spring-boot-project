package com.example.chat.domain.entity.base;

import com.example.chat.domain.entity.base.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Embeddable
@AllArgsConstructor
public class OwnerInfo {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    protected OwnerInfo() {}

    public UUID getOwnerId() {
        return owner.getId();
    }

    public User getOwner() {
        return owner;
    }

    public boolean isOwnedByUserId(UUID userId) {
        return getOwnerId() != null &&
                getOwnerId().equals(userId);
    }

    public boolean isOwnedByUser(User user) {
        return getOwnerId() != null &&
                user != null && getOwnerId().equals(user.getId());
    }

    public boolean hasOwner() {
        return getOwnerId() != null;
    }

}
