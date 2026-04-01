package com.example.chat.host.controller.websocket.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
@AllArgsConstructor
public class DeleteMessageWebSocketDto {
    private UUID messageId;
}