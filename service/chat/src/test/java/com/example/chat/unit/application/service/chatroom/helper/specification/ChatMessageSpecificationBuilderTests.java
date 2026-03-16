package com.example.chat.unit.application.service.chatroom.helper.specification;

import com.example.chat.application.service.chatroom.helper.specification.ChatMessageSpecificationBuilderImpl;
import com.example.chat.application.service.chatroom.helper.specification.data.MessageCursorCriteria;
import com.example.chat.domain.entity.ChatMessage;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
public class ChatMessageSpecificationBuilderTests {

    private ChatMessageSpecificationBuilderImpl builder = new ChatMessageSpecificationBuilderImpl();

    @Test
    void shouldBuildSpecificationForFirstPage() {
        // Arrange
        MessageCursorCriteria criteria = MessageCursorCriteria.firstPage(UUID.randomUUID());

        // Act
        Specification<ChatMessage> spec = builder.fromCursorCriteria(criteria);

        // Assert
        assertNotNull(spec);
    }

    @Test
    void shouldBuildSpecificationWithAscendingCursor() {
        // Arrange
        MessageCursorCriteria criteria = MessageCursorCriteria.builder()
                .chatRoomId(UUID.randomUUID())
                .createdAt(Optional.of(LocalDateTime.now()))
                .direction(Sort.Direction.ASC)
                .build();

        // Act
        Specification<ChatMessage> spec = builder.fromCursorCriteria(criteria);

        // Assert
        assertNotNull(spec);
    }

    @Test
    void shouldBuildSpecificationWithDescendingCursor() {
        // Arrange
        MessageCursorCriteria criteria = MessageCursorCriteria.builder()
                .chatRoomId(UUID.randomUUID())
                .createdAt(Optional.of(LocalDateTime.now()))
                .direction(Sort.Direction.DESC)
                .build();

        // Act
        Specification<ChatMessage> spec = builder.fromCursorCriteria(criteria);

        // Assert
        assertNotNull(spec);
    }

}