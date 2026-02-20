package com.example.bulletin.unit.domain.vo;

import com.example.bulletin.domain.vo.TradeAccountData;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TradeAccountDataTests {

    @Test
    void shouldReturnTrueWhenAllFieldsMatchExceptId() {
        // Arrange
        UUID ownerId = UUID.randomUUID();
        UUID imageId = UUID.randomUUID();
        TradeAccountData data1 = TradeAccountData.builder()
                .id(UUID.randomUUID())
                .ownerId(ownerId)
                .name("name")
                .phone("phone")
                .contacts("contact")
                .description("description")
                .latitude(32.3)
                .longitude(23.3)
                .locationName("location")
                .coordinatesExact(false)
                .approved(false)
                .imageId(imageId)
                .build();

        TradeAccountData data2 = TradeAccountData.builder()
                .id(UUID.randomUUID())
                .ownerId(ownerId)
                .name("name")
                .phone("phone")
                .contacts("contact")
                .description("description")
                .latitude(32.3)
                .longitude(23.3)
                .locationName("location")
                .coordinatesExact(false)
                .approved(false)
                .imageId(imageId)
                .build();

        // Act
        boolean result = data1.equalsData(data2);

        // Assert
        assertTrue(result);
    }

}
