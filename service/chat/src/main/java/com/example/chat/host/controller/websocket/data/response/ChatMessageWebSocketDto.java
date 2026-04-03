package com.example.chat.host.controller.websocket.data.response;

import com.example.chat.application.data.response.ChatMessageResponse;
import com.example.chat.domain.enums.ChatMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageWebSocketDto {
    private UUID id;
    private UUID senderId;
    private ChatMessageType type;
    private boolean updated;
    private String content;
    private LocalDateTime createdAt;


    public static ChatMessageWebSocketDto fromResponse(ChatMessageResponse response) {
        return ChatMessageWebSocketDto.builder()
                .id(response.getId())
                .senderId(response.getSenderId())
                .type(response.getType())
                .updated(response.isUpdated())
                .content(response.getContent())
                .createdAt(response.getCreatedAt())
                .build();
    }

}
