package com.example.bulletin.unit.application.service.tradeaccount.data.response.data;

import com.example.bulletin.application.service.tradeaccount.data.response.data.TradeAccountResponse;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TradeAccountResponseTests {

    @Test
    void shouldReturnTrueWhenAllFieldsMatchExceptId() {
        // Arrange
        UUID ownerId = UUID.randomUUID();
        UUID imageId = UUID.randomUUID();
        TradeAccountResponse data1 = TradeAccountResponse.builder()
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

        TradeAccountResponse data2 = TradeAccountResponse.builder()
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
