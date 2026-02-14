package com.example.bulletin.infrastructure.repository;

import com.example.bulletin.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByNameAndParentId(String name, UUID parentId);
    Category findByNameAndParentId(String name, UUID parentId);
}
