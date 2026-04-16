package com.example.bulletin.integration.hosts.helper.initializer;

import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.entity.TradeAccount;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import com.example.bulletin.infrastructure.repository.BulletinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BulletinInitializer {

    @Autowired
    private BulletinRepository bulletinRepository;

    public Bulletin createDraft(TradeAccount tradeAccount) {
        Bulletin bulletin = Bulletin.createDraft(tradeAccount.getOwnerInfo());
        bulletin.setState(BulletinState.MODIFIABLE);
        bulletinRepository.save(bulletin);
        return bulletin;
    }

}
