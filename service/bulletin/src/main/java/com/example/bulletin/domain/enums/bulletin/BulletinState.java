package com.example.bulletin.domain.enums.bulletin;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum BulletinState {
    ACTIVE,
    INACTIVE,

    CREATED,
    MODIFIABLE,
    APPROVED,

    PUBLISHED,

    COMPLETED;
}
