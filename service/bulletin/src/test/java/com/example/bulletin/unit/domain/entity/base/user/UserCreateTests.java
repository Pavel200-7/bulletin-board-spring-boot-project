package com.example.bulletin.unit.domain.entity.base.user;

import com.example.bulletin.application.mapper.UserMapper;
import com.example.bulletin.domain.vo.UserData;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.bulletin.domain.entity.base.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserCreateTests {

    @Autowired
    private UserMapper mapper;

    @Test
    public void shouldCreateUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        UserData expected = expectedUserData(userId, email, false);

        // Act
        User user = User.createUser(userId, email);
        UserData actual = mapper.toData(user);

        // Assert
        assertTrue(expected.equalsData(actual));
        assertEquals(userId, user.getId());
        assertEquals(email, user.getEmail());
        assertFalse(user.isBlocked());
    }

    private UserData expectedUserData(UUID userId, String email, boolean blocked) {
        return UserData.builder()
                .id(userId)
                .email(email)
                .blocked(blocked)
                .build();
    }

}