package com.example.bulletin.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum BulletinStatus {
    DRAFT(1, "Черновик", "Первое состояние объявления, можно изменять, видно только создателю."),
    PUBLISHED(2, "Опубликовано", "Опубликованное из черновика объявление, нельзя изменять, видно всем."),
    CLOSED(3, "Закрыто", "Закрыто после публикации, нельзя изменять, видно только создателю.");

    private final int code;
    private final String title;
    private final String description;

    public static BulletinStatus fromCode(int code) {
        return Arrays.stream(BulletinStatus.values())
                .filter(st -> st.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown code: " + code));
    }

}
