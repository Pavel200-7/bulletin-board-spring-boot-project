package com.example.chat.application.service.chatroom.helper.specification;

import com.example.chat.application.service.chatroom.helper.specification.data.MessageCursorCriteria;
import com.example.chat.domain.entity.ChatMessage;
import org.springframework.data.jpa.domain.Specification;

public interface ChatMessageSpecificationBuilder {
    Specification<ChatMessage> fromCursorCriteria(MessageCursorCriteria criteria);
}
