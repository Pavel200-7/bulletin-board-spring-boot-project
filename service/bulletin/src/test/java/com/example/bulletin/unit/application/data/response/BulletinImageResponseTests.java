package com.example.bulletin.unit.application.data.response;

import com.example.bulletin.application.data.response.BulletinImageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class BulletinImageResponseTests {

    @Test
    void shouldReturnTrueWhenAllFieldsMatchExceptId() {
        // Arrange
        UUID bulletinId = UUID.randomUUID();
        UUID imageId = UUID.randomUUID();

        BulletinImageResponse data1 = BulletinImageResponse.builder()
                .id(UUID.randomUUID())
                .bulletinId(bulletinId)
                .imageId(imageId)
                .build();

        BulletinImageResponse data2 = BulletinImageResponse.builder()
                .id(UUID.randomUUID())
                .bulletinId(bulletinId)
                .imageId(imageId)
                .build();

        // Act
        boolean result = data1.equalsData(data2);

        // Assert
        assertTrue(result);
    }

}
