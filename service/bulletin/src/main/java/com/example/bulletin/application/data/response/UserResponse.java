package com.example.bulletin.application.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Objects;
import java.util.UUID;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
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