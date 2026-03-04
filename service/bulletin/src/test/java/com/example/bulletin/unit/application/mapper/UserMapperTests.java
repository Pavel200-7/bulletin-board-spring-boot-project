package com.example.bulletin.unit.application.mapper;

import com.example.bulletin.application.mapper.UserMapper;
import com.example.bulletin.application.data.response.UserResponse;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.domain.vo.UserData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserMapperTests {

    @Autowired
    private UserMapper mapper;

    @Test
    public void shouldConvertCorrectlyFromEntityToData() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        User user = User.createUser(userId, email);
        user.setBlocked(true);

        UserData expected = UserData.builder()
                .id(user.getId())
                .email(user.getEmail())
                .blocked(user.isBlocked())
                .build();

        // Act
        UserData actual = mapper.toData(user);

        // Assert
        assertTrue(expected.equalsData(actual));
    }

    @Test
    public void shouldConvertCorrectlyFromEntityToResponse() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        User user = User.createUser(userId, email);
        user.setBlocked(true);

        UserResponse expected = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .blocked(user.isBlocked())
                .build();

        // Act
        UserResponse actual = mapper.toResponse(user);

        // Assert
        assertTrue(expected.equalsData(actual));
    }

}