package com.example.bulletin.infrastructure.repository;

import com.example.bulletin.domain.entity.TradeAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TradeAccountRepository extends JpaRepository<TradeAccount, UUID> {
    @Query("select t from TradeAccount t where t.ownerInfo.owner.id = :ownerId")
    Optional<TradeAccount> findByOwnerInfoOwnerId(@Param("ownerId") UUID ownerId);
}
