package com.example.bulletin.unit.domain.entity.tradeaccount;

import com.example.bulletin.application.mapper.TradeAccountMapper;
import com.example.bulletin.domain.entity.TradeAccount;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.domain.vo.TradeAccountData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class TradeAccountCreateTests {

    @Autowired
    private TradeAccountMapper mapper;

    @Test
    public void shouldCreateTradeAccount() {
        // Arrange
        OwnerInfo ownerInfo = createOwnerInfo();
        TradeAccountData expected = TradeAccountData.builder()
                .ownerId(ownerInfo.getOwnerId())
                .name(null)
                .phone(null)
                .contacts(null)
                .description(null)
                .latitude(null)
                .longitude(null)
                .locationName(null)
                .coordinatesExact(false)
                .approved(false)
                .imageId(null)
                .build();

        // Act
        TradeAccount tradeAccount = TradeAccount.createTradeAccount(ownerInfo);
        TradeAccountData actual = mapper.toData(tradeAccount);

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    private OwnerInfo createOwnerInfo() {
        User user = User.createUser(UUID.randomUUID(), "owner@example.com");
        return new OwnerInfo(user);
    }

}
