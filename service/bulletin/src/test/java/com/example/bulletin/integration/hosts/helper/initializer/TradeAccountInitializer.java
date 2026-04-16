package com.example.bulletin.integration.hosts.helper.initializer;

import com.example.bulletin.domain.entity.TradeAccount;
import com.example.bulletin.domain.entity.base.Location;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.infrastructure.repository.TradeAccountRepository;
import com.example.bulletin.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TradeAccountInitializer {

    private final UserRepository userRepository;
    private final TradeAccountRepository tradeAccountRepository;

    public TradeAccount createApprovedTradeAccount(UUID userId) {
        String userRandomEmail = userId.toString().concat("@mail.su");

        User user = User.createUser(userId, userRandomEmail);
        userRepository.save(user);

        OwnerInfo ownerInfo = new OwnerInfo(user);
        TradeAccount tradeAccount = TradeAccount.createTradeAccount(ownerInfo);
        tradeAccount.setName("Test Account");
        tradeAccount.setPhone("+7 (999) 123-45-67");
        tradeAccount.setImageId(UUID.randomUUID());
        tradeAccount.setExactLocation(new Location(55.7558, 37.6173, "Moscow", ""));
        tradeAccount.approve();
        tradeAccountRepository.save(tradeAccount);

        return tradeAccount;
    }


}
