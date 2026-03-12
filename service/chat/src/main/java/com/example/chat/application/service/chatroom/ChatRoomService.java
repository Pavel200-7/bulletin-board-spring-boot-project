package com.example.chat.application.service.chatroom;

import com.example.chat.domain.entity.ChatMessage;

import java.awt.print.Pageable;
import java.util.List;
import java.util.UUID;

public interface ChatRoomService {
    List<ChatMessage> getMessages(UUID chatRoomId, Pageable pageable);
}
