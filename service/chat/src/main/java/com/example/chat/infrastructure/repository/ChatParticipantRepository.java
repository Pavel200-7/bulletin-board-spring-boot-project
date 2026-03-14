package com.example.chat.infrastructure.repository;

import com.example.chat.domain.entity.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, UUID> {

    List<ChatParticipant> findByProfileId(UUID profileId);

    Optional<ChatParticipant> findByChatRoomIdAndProfileId(UUID chatRoomId, UUID profileId);

    @Query("SELECT cp.chatRoom.id FROM ChatParticipant cp WHERE cp.profile.id = :profileId")
    List<UUID> findChatRoomIdsByProfileId(@Param("profileId") UUID profileId);
}