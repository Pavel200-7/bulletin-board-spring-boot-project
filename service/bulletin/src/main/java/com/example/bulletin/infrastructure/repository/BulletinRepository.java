package com.example.bulletin.infrastructure.repository;

import com.example.bulletin.domain.entity.Bulletin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BulletinRepository extends JpaRepository<Bulletin, UUID> {
    public boolean existsByCategoryId(UUID categoryId);
}
