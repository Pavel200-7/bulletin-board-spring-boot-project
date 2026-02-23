package com.example.bulletin.unit.domain.entity.tradeaccount;

import com.example.bulletin.domain.entity.TradeAccount;
import com.example.bulletin.domain.entity.base.Location;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TradeAccountUpdateTests {

    @Test
    public void shouldSetApproximateLocation() {
        // Arrange
        TradeAccount tradeAccount = createBlankTradeAccount();
        Location location = createLocation();

        // Act
        tradeAccount.setApproximateLocation(location);

        // Assert
        assertEquals(location, tradeAccount.getLocation());
        assertFalse(tradeAccount.isCoordinatesExact());
    }

    @Test
    public void shouldThrowWhenSetApproximateLocationForApprovedAccount() {
        // Arrange
        TradeAccount tradeAccount = createApprovedTradeAccount();
        Location location = createLocation();

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                tradeAccount.setApproximateLocation(location));
    }

    @Test
    public void shouldSetExactLocation() {
        // Arrange
        TradeAccount tradeAccount = createBlankTradeAccount();
        Location location = createLocation();

        // Act
        tradeAccount.setExactLocation(location);

        // Assert

        assertEquals(location, tradeAccount.getLocation());
        assertTrue(tradeAccount.isCoordinatesExact());
    }

    @Test
    public void shouldApproveAccount() {
        // Arrange
        TradeAccount tradeAccount = createTradeAccountReadyForApprove();

        // Act
        tradeAccount.approve();

        // Assert
        assertTrue(tradeAccount.isApproved());

    }

    @Test
    public void shouldThrowWhenApproveAccountWithoutExactLocation() {
        // Arrange
        TradeAccount tradeAccount = createTradeAccountReadyForApprove();
        tradeAccount.setApproximateLocation(createLocation());

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                tradeAccount.approve());
    }

    @Test
    public void shouldThrowWhenApproveAlreadyApprovedAccount() {
        // Arrange
        TradeAccount tradeAccount = createApprovedTradeAccount();

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                tradeAccount.approve());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "")
    public void shouldThrowWhenApproveAccountWithoutName(String name) {
        // Arrange
        TradeAccount tradeAccount = createTradeAccountReadyForApprove();
        tradeAccount.setName(name);

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                tradeAccount.approve());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "")
    public void shouldThrowWhenApproveAccountWithoutPhone(String phone) {
        // Arrange
        TradeAccount tradeAccount = createTradeAccountReadyForApprove();
        tradeAccount.setPhone(phone);

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                tradeAccount.approve());
    }

    private TradeAccount createApprovedTradeAccount() {
        TradeAccount tradeAccount = createTradeAccountReadyForApprove();
        tradeAccount.approve();
        return tradeAccount;
    }

    private TradeAccount createTradeAccountReadyForApprove() {
        TradeAccount tradeAccount = createBlankTradeAccount();
        tradeAccount.setName("Test Account");
        tradeAccount.setPhone("+7 (999) 123-45-67");
        tradeAccount.setImageId(UUID.randomUUID());

        tradeAccount.setExactLocation(createLocation());

        return tradeAccount;
    }

    private TradeAccount createBlankTradeAccount() {
        User user = User.createUser(UUID.randomUUID(), "owner@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return TradeAccount.createTradeAccount(ownerInfo);
    }

    private Location createLocation() {
        return new Location(55.7558, 37.6173, "Moscow", "");
    }

}