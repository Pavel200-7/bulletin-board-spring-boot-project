package com.example.bulletin.unit.domain.entity.tradeaccount;

import com.example.bulletin.application.mapper.TradeAccountMapper;
import com.example.bulletin.domain.entity.TradeAccount;
import com.example.bulletin.domain.entity.base.Location;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.domain.vo.TradeAccountData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class TradeAccountUpdateTests {

    @Autowired
    private TradeAccountMapper mapper;

    @Test
    public void shouldSetApproximateLocation() {
        // Arrange
        TradeAccount tradeAccount = createUnapprovedTradeAccount();
        Location location = createLocation();
        TradeAccountData expected = mapper.toData(tradeAccount);
        expected = TradeAccountData.builder()
                .ownerId(expected.getOwnerId())
                .name(expected.getName())
                .phone(expected.getPhone())
                .contacts(expected.getContacts())
                .description(expected.getDescription())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .townName(location.getTownName())
                .locationName(location.getLocationName())
                .coordinatesExact(false)
                .approved(false)
                .imageId(expected.getImageId())
                .build();

        // Act
        tradeAccount.setApproximateLocation(location);
        TradeAccountData actual = mapper.toData(tradeAccount);

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
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
        TradeAccount tradeAccount = createUnapprovedTradeAccount();
        Location location = createLocation();
        TradeAccountData expected = mapper.toData(tradeAccount);
        expected = TradeAccountData.builder()
                .ownerId(expected.getOwnerId())
                .name(expected.getName())
                .phone(expected.getPhone())
                .contacts(expected.getContacts())
                .description(expected.getDescription())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .townName(location.getTownName())
                .locationName(location.getLocationName())
                .coordinatesExact(true)
                .approved(false)
                .imageId(expected.getImageId())
                .build();

        // Act
        tradeAccount.setExactLocation(location);
        TradeAccountData actual = mapper.toData(tradeAccount);

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    public void shouldApproveAccount() {
        // Arrange
        TradeAccount tradeAccount = createTradeAccountWithExactLocation();
        TradeAccountData expected = mapper.toData(tradeAccount);
        expected = TradeAccountData.builder()
                .ownerId(expected.getOwnerId())
                .name(expected.getName())
                .phone(expected.getPhone())
                .contacts(expected.getContacts())
                .description(expected.getDescription())
                .latitude(expected.getLatitude())
                .longitude(expected.getLongitude())
                .townName(expected.getTownName())
                .locationName(expected.getLocationName())
                .coordinatesExact(true)
                .approved(true)
                .imageId(expected.getImageId())
                .build();

        // Act
        tradeAccount.approve();
        TradeAccountData actual = mapper.toData(tradeAccount);

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    public void shouldThrowWhenApproveAccountWithoutExactLocation() {
        // Arrange
        TradeAccount tradeAccount = createTradeAccountWithApproximateLocation();

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
        TradeAccount tradeAccount = createTradeAccountWithExactLocation();
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
        TradeAccount tradeAccount = createTradeAccountWithExactLocation();
        tradeAccount.setPhone(phone);

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                tradeAccount.approve());
    }

    private TradeAccount createUnapprovedTradeAccount() {
        OwnerInfo ownerInfo = createOwnerInfo();
        TradeAccount tradeAccount = TradeAccount.createTradeAccount(ownerInfo);
        tradeAccount.setName("Test Account");
        tradeAccount.setPhone("+7 (999) 123-45-67");
        tradeAccount.setImageId(UUID.randomUUID());
        return tradeAccount;
    }

    private TradeAccount createTradeAccountWithExactLocation() {
        TradeAccount tradeAccount = createUnapprovedTradeAccount();
        tradeAccount.setExactLocation(createLocation());
        return tradeAccount;
    }

    private TradeAccount createTradeAccountWithApproximateLocation() {
        TradeAccount tradeAccount = createUnapprovedTradeAccount();
        tradeAccount.setApproximateLocation(createLocation());
        return tradeAccount;
    }

    private TradeAccount createApprovedTradeAccount() {
        TradeAccount tradeAccount = createTradeAccountWithExactLocation();
        tradeAccount.approve();
        return tradeAccount;
    }

    private OwnerInfo createOwnerInfo() {
        User user = User.createUser(UUID.randomUUID(), "owner@example.com");
        return new OwnerInfo(user);
    }

    private Location createLocation() {
        return new Location(55.7558, 37.6173, "Moscow", "");
    }

}