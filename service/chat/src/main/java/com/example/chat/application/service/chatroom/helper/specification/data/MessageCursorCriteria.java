package com.example.chat.application.service.chatroom.helper.specification.data;

import com.example.chat.domain.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort.Direction;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageCursorCriteria {
    private UUID chatRoomId;
    private Optional<LocalDateTime> createdAt;
    private Direction direction;

    public static MessageCursorCriteria fromChatMessage(UUID chatRoomId, ChatMessage message, Direction direction) {
        LocalDateTime cursorTime = null;
        if (message.getCreatedAt() != null) {
            cursorTime = message.getCreatedAt();
        }

        return MessageCursorCriteria.builder()
                .chatRoomId(chatRoomId)
                .createdAt(Optional.ofNullable(cursorTime))
                .direction(direction)
                .build();


    }

    public static MessageCursorCriteria firstPage(UUID chatRoomId) {
        return MessageCursorCriteria.builder()
                .chatRoomId(chatRoomId)
                .createdAt(Optional.empty())
                .direction(Direction.ASC)
                .build();
    }

}
