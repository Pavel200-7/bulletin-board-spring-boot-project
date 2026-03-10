package com.example.bulletin.infrastructure.repository;

import com.example.bulletin.domain.entity.Bulletin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BulletinRepository extends JpaRepository<Bulletin, UUID>,
        JpaSpecificationExecutor<Bulletin> {

    boolean existsByCategoryId(UUID categoryId);

    @Query(value = """
            SELECT b FROM Bulletin b
            LEFT JOIN FETCH b.category
            LEFT JOIN FETCH b.characteristics bc
                LEFT JOIN FETCH bc.name
                LEFT JOIN FETCH bc.value
            WHERE b.id = :id
            """)
    Optional<Bulletin> findByIdEager(@Param("id") UUID id);
}
