package com.example.bulletin.unit.domain.vo;

import com.example.bulletin.domain.vo.UserData;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDataTests {

    @Test
    void shouldReturnTrueWhenAllFieldsMatchExceptId() {
        // Arrange
        UserData data1 = UserData.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .blocked(false)
                .build();

        UserData data2 = UserData.builder()
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