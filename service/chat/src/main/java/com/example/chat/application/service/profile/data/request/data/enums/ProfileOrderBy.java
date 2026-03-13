package com.example.chat.application.service.profile.data.request.data.enums;

public enum ProfileOrderBy {
    PUBLIC_NAME("publicName"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");

    private final String fieldName;

    ProfileOrderBy(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}