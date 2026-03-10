package com.example.bulletin.application.service.bulletin.data.request.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BulletinOrderBy {
    TITLE("title"),
    PRICE("price");

    private final String fieldName;
}
