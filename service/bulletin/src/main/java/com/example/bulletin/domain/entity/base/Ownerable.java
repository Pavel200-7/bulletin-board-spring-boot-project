package com.example.bulletin.domain.entity.base;

import com.example.bulletin.domain.entity.base.User.User;

import java.util.UUID;

public interface Ownerable {

    public UUID getOwnerId();

    public User getOwner();

}
