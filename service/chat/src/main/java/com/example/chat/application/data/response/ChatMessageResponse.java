package com.example.chat.application.data.response;

import com.example.chat.domain.enums.ChatMessageType;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class ChatMessageResponse {
    private UUID id;
    private UUID senderId;
    private ChatMessageType type;
    private boolean updated;
    private String content;
}
