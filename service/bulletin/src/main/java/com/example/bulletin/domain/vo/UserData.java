package com.example.bulletin.domain.vo;

import lombok.Builder;
import lombok.Value;

import java.util.Objects;
import java.util.UUID;

@Value
@Builder
public class UserData {
    private UUID id;
    private String email;
    private boolean blocked;

    public boolean equalsData(UserData other) {
        if (other == null) return false;
        return Objects.equals(email, other.email) &&
                Objects.equals(blocked, other.blocked);
    }

}