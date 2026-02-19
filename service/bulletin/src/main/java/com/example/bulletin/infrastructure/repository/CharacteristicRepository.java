package com.example.bulletin.infrastructure.repository;

import com.example.bulletin.domain.entity.Characteristic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CharacteristicRepository extends JpaRepository<Characteristic, UUID> {

    @Query(value = """
            WITH RECURSIVE
            -- Предки (включая исходную)
            ancestors AS (
                 SELECT id, parent_id
                 FROM categories
                 WHERE id = :categoryId
        
                 UNION ALL
        
                 SELECT c.id, c.parent_id
                 FROM categories c
                 JOIN ancestors a ON c.id = a.parent_id
            )
            
            SELECT ch.*
            FROM  characteristics ch
            JOIN ancestors ac ON ch.category_id = ac.id
        """, nativeQuery = true)
    List<Characteristic> findByCategoryHierarchy(@Param("categoryId") UUID categoryId);
}
