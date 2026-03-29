package com.example.notification.infrastructure.repository;

import com.example.notification.domain.entity.Subscription;
import com.example.notification.domain.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    List<Subscription> findByOwnerInfo_Owner_Id(UUID ownerId);

    @Query(value = """
            SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
            FROM Subscription s
            WHERE s.ownerInfo.owner.id = :ownerId
            AND s.type = :type
            AND s.publisherId = :publisherId
            """)
    boolean existsByOwnerTypeAndPublisher(
            @Param("ownerId") UUID ownerId,
            @Param("type") NotificationType type,
            @Param("publisherId") UUID publisherId
    );

    @Query(value = """
            SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
            FROM Subscription s
            WHERE s.ownerInfo.owner.id = :ownerId
            AND s.type = :type
            AND s.publisherId = :publisherId
            """)
    boolean existsByCurrentUserTypeAndPublisher(
            @Param("ownerId") UUID ownerId,
            @Param("type") NotificationType type,
            @Param("publisherId") UUID publisherId
    );

    @Query(value = """
        SELECT DISTINCT s 
        FROM Subscription s 
        JOIN FETCH s.ownerInfo.owner u
        WHERE s.type = :type 
        AND s.publisherId = :publisherId
        """)
    Page<Subscription> findPageByTypeAndPublisher(
            @Param("type") NotificationType type,
            @Param("publisherId") UUID publisherId,
            Pageable pageable);

}
