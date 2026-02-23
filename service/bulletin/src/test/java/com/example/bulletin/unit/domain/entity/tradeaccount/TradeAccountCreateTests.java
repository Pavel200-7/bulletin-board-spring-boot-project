package com.example.bulletin.unit.domain.entity.tradeaccount;

import com.example.bulletin.domain.entity.TradeAccount;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TradeAccountCreateTests {

    @Test
    public void shouldCreateTradeAccount() {
        // Arrange
        OwnerInfo ownerInfo = createOwnerInfo();

        // Act
        TradeAccount tradeAccount = TradeAccount.createTradeAccount(ownerInfo);

        // Assert
        assertEquals(ownerInfo.getOwner(),
                tradeAccount.getOwner());
        assertFalse(tradeAccount.isApproved());

    }

    private OwnerInfo createOwnerInfo() {
        User user = User.createUser(UUID.randomUUID(), "owner@example.com");
        return new OwnerInfo(user);
    }

}
