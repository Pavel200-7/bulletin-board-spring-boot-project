package com.example.bulletin.unit.application.mapper;

import com.example.bulletin.application.mapper.TradeAccountMapper;
import com.example.bulletin.application.data.response.TradeAccountResponse;
import com.example.bulletin.application.mapper.UserMapper;
import com.example.bulletin.domain.entity.TradeAccount;
import com.example.bulletin.domain.entity.base.Location;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.domain.vo.TradeAccountData;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class TradeAccountMapperTests {

    private TradeAccountMapper mapper = Mappers.getMapper(
            TradeAccountMapper.class);

    @Test
    public void shouldConvertCorrectlyFromEntityToData() {
        // Arrange
        TradeAccount tradeAccount = createTradeAccount();

        TradeAccountData expected = TradeAccountData.builder()
                .id(tradeAccount.getId())
                .ownerId(tradeAccount.getOwnerInfo().getOwnerId())
                .name(tradeAccount.getName())
                .phone(tradeAccount.getPhone())
                .contacts(tradeAccount.getContacts())
                .description(tradeAccount.getDescription())
                .latitude(tradeAccount.getLocation().getLatitude())
                .longitude(tradeAccount.getLocation().getLongitude())
                .townName(tradeAccount.getLocation().getTownName())
                .locationName(tradeAccount.getLocation().getLocationName())
                .coordinatesExact(tradeAccount.isCoordinatesExact())
                .approved(tradeAccount.isApproved())
                .imageId(tradeAccount.getImageId())
                .build();

        // Act
        TradeAccountData actual = mapper.toData(tradeAccount);

        // Assert
        assertTrue(expected.equalsData(actual));
    }

    @Test
    public void shouldConvertCorrectlyFromEntityToResponse() {
        // Arrange
        TradeAccount tradeAccount = createTradeAccount();

        TradeAccountResponse expected = TradeAccountResponse.builder()
                .id(tradeAccount.getId())
                .ownerId(tradeAccount.getOwnerInfo().getOwnerId())
                .name(tradeAccount.getName())
                .phone(tradeAccount.getPhone())
                .contacts(tradeAccount.getContacts())
                .description(tradeAccount.getDescription())
                .latitude(tradeAccount.getLocation().getLatitude())
                .longitude(tradeAccount.getLocation().getLongitude())
                .townName(tradeAccount.getLocation().getTownName())
                .locationName(tradeAccount.getLocation().getLocationName())
                .coordinatesExact(tradeAccount.isCoordinatesExact())
                .approved(tradeAccount.isApproved())
                .imageId(tradeAccount.getImageId())
                .build();

        // Act
        TradeAccountResponse actual = mapper.toResponse(tradeAccount);

        // Assert
        assertTrue(expected.equalsData(actual));
    }

    private TradeAccount createTradeAccount() {
        User user = createUser();
        OwnerInfo ownerInfo = new OwnerInfo(user);

        TradeAccount tradeAccount = TradeAccount.createTradeAccount(ownerInfo);
        tradeAccount.setName("Test Trade Account");
        tradeAccount.setPhone("+7 (999) 123-45-67");
        tradeAccount.setContacts("Telegram: @test, Email: test@example.com");
        tradeAccount.setDescription("Test description for trade account");
        tradeAccount.setExactLocation(createLocation());
        tradeAccount.setImageId(UUID.randomUUID());
        tradeAccount.approve();

        return tradeAccount;
    }

    private User createUser() {
        UUID userId = UUID.randomUUID();
        String email = "some@mail.ru";
        return User.createUser(userId, email);
    }

    private Location createLocation() {
        return new Location(55.7558, 37.6173, "Moscow", "Moscow, some street...");
    }

}