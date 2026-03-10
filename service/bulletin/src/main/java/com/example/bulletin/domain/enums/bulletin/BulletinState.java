package com.example.bulletin.domain.enums.bulletin;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum BulletinState {
    CREATED,
    MODIFIABLE,
    APPROVED,

    PUBLISHED,

    COMPLETED;
}
