package com.example.bulletin.unit.application.service.user.data.response.data;

import com.example.bulletin.application.service.user.data.response.data.UserResponse;
import com.example.bulletin.domain.vo.UserData;
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