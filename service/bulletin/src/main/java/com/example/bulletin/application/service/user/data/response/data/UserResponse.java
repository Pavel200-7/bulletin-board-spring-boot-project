package com.example.bulletin.application.service.user.data.response.data;

import lombok.Builder;
import lombok.Value;

import java.util.Objects;
import java.util.UUID;

@Value
@Builder
public class UserResponse {
    private UUID id;
    private String email;
    private boolean blocked;

    public boolean equalsData(UserResponse other) {
        if (other == null) return false;
        return Objects.equals(email, other.email) &&
                Objects.equals(blocked, other.blocked);
    }
}