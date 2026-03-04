package com.example.bulletin.unit.application.data.response;

import com.example.bulletin.application.data.response.UserResponse;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserResponseTests {

    @Test
    void shouldReturnTrueWhenAllFieldsMatchExceptId() {
        // Arrange
        UserResponse data1 = UserResponse.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .blocked(false)
                .build();

        UserResponse data2 = UserResponse.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .blocked(false)
                .build();

        // Act
        boolean result = data1.equalsData(data2);

        // Assert
        assertTrue(result);
    }

}