package com.example.chat.infrastructure.repository;

import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository  extends JpaRepository<Profile, UUID>,
        JpaSpecificationExecutor<Profile> {
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM Profile p WHERE p.ownerInfo.owner.id = :ownerId")
    boolean existsByOwnerInfoOwnerId(@Param("ownerId") UUID ownerId);

    @Query("SELECT p FROM Profile p WHERE p.ownerInfo.owner.id = :ownerId")
    Optional<Profile> findByOwnerInfoOwnerId(@Param("ownerId") UUID ownerId);
}
