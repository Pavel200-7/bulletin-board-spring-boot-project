package com.example.bulletin.unit.domain.vo;

import com.example.bulletin.domain.vo.BulletinImageData;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BulletinImageDataTests {

    @Test
    void shouldReturnTrueWhenAllFieldsMatchExceptId() {
        // Arrange
        UUID bulletinId = UUID.randomUUID();
        UUID imageId = UUID.randomUUID();

        BulletinImageData data1 = BulletinImageData.builder()
                .id(UUID.randomUUID())
                .bulletinId(bulletinId)
                .imageId(imageId)
                .build();

        BulletinImageData data2 = BulletinImageData.builder()
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
