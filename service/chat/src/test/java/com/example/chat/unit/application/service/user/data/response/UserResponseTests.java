package com.example.chat.unit.application.service.user.data.response;

import com.example.notification.application.data.response.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
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