package com.example.chat.host.controller.websocket.data.response.base;

import com.example.chat.host.controller.websocket.data.response.enums.WebSocketMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketMessageDto {
    private WebSocketMessageType type;
    private Object data;
}
