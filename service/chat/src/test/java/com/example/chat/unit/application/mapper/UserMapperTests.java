package com.example.chat.unit.application.mapper;

import com.example.chat.application.data.response.UserResponse;
import com.example.chat.application.mapper.UserMapper;
import com.example.chat.domain.entity.base.user.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class UserMapperTests {

    private UserMapper mapper = Mappers.getMapper(
            UserMapper.class);

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