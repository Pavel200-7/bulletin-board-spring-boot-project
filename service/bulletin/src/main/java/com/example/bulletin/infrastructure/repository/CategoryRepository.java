package com.example.bulletin.infrastructure.repository;

import com.example.bulletin.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByNameAndParentId(String name, UUID parentId);
    Category findByNameAndParentId(String name, UUID parentId);
    List<Category> findByParentId(UUID parentId);

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
            ),
            -- Потомки (исключая исходную - она уже есть в ancestors)
            descendants AS (
                SELECT id, parent_id
                FROM categories
                WHERE parent_id = :categoryId
                
                UNION ALL
                
                SELECT c.id, c.parent_id
                FROM categories c
                JOIN descendants d ON c.parent_id = d.id
            ),
            all_categories AS (
                SELECT id, parent_id FROM ancestors
                UNION ALL
                SELECT id, parent_id FROM descendants
            )
            SELECT EXISTS(
                SELECT 1
                FROM characteristics ch
                JOIN all_categories ac ON ch.category_id = ac.id
                WHERE LOWER(ch.name) = LOWER(:name)
            )
        """, nativeQuery = true)
    boolean existsCharacteristicWithNameInHierarchy(
            @Param("categoryId") UUID categoryId,
            @Param("name") String name
    );
}
