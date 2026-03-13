package com.example.chat.infrastructure.repository;

import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository  extends JpaRepository<Profile, UUID>,
        JpaSpecificationExecutor<Profile> {
    boolean existsByOwnerInfoOwnerId(UUID ownerId);
    Optional<Profile> findByOwnerInfoOwnerId(UUID ownerId);
}
