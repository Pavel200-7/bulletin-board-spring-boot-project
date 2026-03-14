package com.example.chat.infrastructure.repository;

import com.example.chat.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {

    @Query("SELECT cr FROM ChatRoom cr JOIN cr.participants p1 JOIN cr.participants p2 " +
            "WHERE cr.type = TWO_PARTY AND p1.profile.id = :profile1Id AND p2.profile.id = :profile2Id")
    Optional<ChatRoom> findTwoPartyChatBetween(@Param("profile1Id") UUID profile1Id,
                                               @Param("profile2Id") UUID profile2Id);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.id IN :ids")
    List<ChatRoom> findByIds(@Param("ids") List<UUID> ids);
}