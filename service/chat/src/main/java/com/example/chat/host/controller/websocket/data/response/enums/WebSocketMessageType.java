package com.example.chat.host.controller.websocket.data.response.enums;

import lombok.Getter;

@Getter
public enum WebSocketMessageType {
    MESSAGE_CREATED,
    MESSAGE_UPDATED,
    MESSAGE_DELETED;
}
