package com.example.chat.unit.domain.entity.base.user;


import com.example.chat.domain.entity.base.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ActiveProfiles("test")
public class UserCreateTests {

    @Test
    public void shouldCreateUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";

        // Act
        User user = User.createUser(userId, email);

        // Assert
        assertEquals(userId, user.getId());
        assertEquals(email, user.getEmail());
        assertFalse(user.isBlocked());
    }

}