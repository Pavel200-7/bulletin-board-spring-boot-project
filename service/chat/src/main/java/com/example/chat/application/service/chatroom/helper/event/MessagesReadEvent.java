package com.example.chat.application.service.chatroom.helper.event;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class MessagesReadEvent {
    UUID chatRoomId;
    UUID readerProfileId;
    UUID lastReadMessageId;
    LocalDateTime lastReadTimestamp;
}
