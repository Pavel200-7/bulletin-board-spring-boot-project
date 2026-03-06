package com.example.bulletin.infrastructure.repository;

import com.example.bulletin.domain.entity.TradeAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TradeAccountRepository extends JpaRepository<TradeAccount, UUID> {
    Optional<TradeAccount> findByOwnerInfo_Owner_Id(UUID ownerId);
}
