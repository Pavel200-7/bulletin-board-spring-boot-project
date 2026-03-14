package com.example.chat.application.service.chatroom.helper.specification.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort.Direction;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageCursorCriteria {
    private UUID chatRoomId;
    private LocalDateTime createdAt;
    private Direction direction;
}
