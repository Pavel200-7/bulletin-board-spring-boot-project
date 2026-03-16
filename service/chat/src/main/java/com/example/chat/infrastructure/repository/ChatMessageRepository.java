package com.example.chat.infrastructure.repository;

import com.example.chat.domain.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID>,
        JpaSpecificationExecutor<ChatMessage> {

    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chatRoom.id = :chatRoomId " +
            "AND m.createdAt > (SELECT m2.createdAt FROM ChatMessage m2 " +
            "WHERE m2.id = (SELECT p.lastReadMessageId FROM ChatParticipant p " +
            "WHERE p.profile.id = :profileId AND p.chatRoom.id = :chatRoomId))")
    int countUnreadMessages(@Param("chatRoomId") UUID chatRoomId,
                            @Param("profileId") UUID profileId);

    @Query("SELECT MAX(m.createdAt) FROM ChatMessage m WHERE m.chatRoom.id = :chatRoomId")
    LocalDateTime findLastMessageTime(@Param("chatRoomId") UUID chatRoomId);

    @Modifying
    @Query("UPDATE ChatMessage m SET m.read = true " +
            "WHERE m.chatRoom.id = :chatRoomId " +
            "AND m.sender.id != :readerProfileId " +
            "AND m.read = false " +
            "AND m.createdAt <= (SELECT m2.createdAt FROM ChatMessage m2 " +
            "WHERE m2.id = :lastReadMessageId)")
    int markMessagesAsRead(@Param("chatRoomId") UUID chatRoomId,
                           @Param("readerProfileId") UUID readerProfileId,
                           @Param("lastReadMessageId") UUID lastReadMessageId);
}