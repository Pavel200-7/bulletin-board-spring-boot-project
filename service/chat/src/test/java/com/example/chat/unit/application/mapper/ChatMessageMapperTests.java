package com.example.chat.unit.application.mapper;

import com.example.chat.application.data.response.ChatMessageResponse;
import com.example.chat.application.mapper.ChatMessageMapper;
import com.example.chat.domain.entity.ChatMessage;
import com.example.chat.domain.entity.ChatRoom;
import com.example.chat.domain.entity.Contact;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.entity.base.user.User;
import com.example.chat.domain.enums.ChatMessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class ChatMessageMapperTests {

    private ChatMessageMapper mapper = Mappers.getMapper(ChatMessageMapper.class);

    private UUID senderId;
    private Profile sender;
    private Profile otherParticipant;
    private ChatRoom chatRoom;
    private String textContent;
    private UUID imageId;

    @BeforeEach
    void setUp() {
        senderId = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();

        sender = createProfile(senderId, "Sender");
        otherParticipant = createProfile(otherId, "Other");

        Contact contact = sender.addContact(otherParticipant);
        chatRoom = sender.addChatRoom(contact);

        textContent = "Hello, world!";
        imageId = UUID.randomUUID();
    }

    @Test
    void shouldMapTextMessage() {
        // Arrange
        ChatMessage textMessage = chatRoom.addTextMessage(sender, textContent);

        // Act
        ChatMessageResponse response = mapper.toResponse(textMessage);

        // Assert
        assertNotNull(response);
        assertEquals(textMessage.getId(), response.getId());
        assertEquals(ChatMessageType.TEXT, response.getType());
        assertFalse(response.isUpdated());
        assertEquals(textContent, response.getContent());
    }

    @Test
    void shouldMapImageMessage() {
        // Arrange
        ChatMessage imageMessage = chatRoom.addImageMessage(sender, imageId);

        // Act
        ChatMessageResponse response = mapper.toResponse(imageMessage);

        // Assert
        assertNotNull(response);
        assertEquals(imageMessage.getId(), response.getId());
        assertEquals(ChatMessageType.IMAGE, response.getType());
        assertFalse(response.isUpdated());
        assertEquals(imageId.toString(), response.getContent());
    }

    @Test
    void shouldMapMessageFromOtherParticipant() {
        // Arrange
        ChatMessage message = chatRoom.addTextMessage(otherParticipant, "Message from other");

        // Act
        ChatMessageResponse response = mapper.toResponse(message);

        // Assert
        assertEquals(otherParticipant.getId(), response.getSenderId());
        assertEquals("Message from other", response.getContent());
    }

    @Test
    void shouldMapUpdatedFlag() {
        // Arrange
        ChatMessage message = chatRoom.addTextMessage(sender, "Original");
        message.update("Updated");

        // Act
        ChatMessageResponse response = mapper.toResponse(message);

        // Assert
        assertTrue(response.isUpdated());
        assertEquals("Updated", response.getContent());
    }

    @Test
    void shouldMapMultipleMessages() {
        // Arrange
        ChatMessage msg1 = chatRoom.addTextMessage(sender, "First");
        ChatMessage msg2 = chatRoom.addImageMessage(sender, imageId);
        ChatMessage msg3 = chatRoom.addTextMessage(otherParticipant, "Third");
        msg3.update("Third updated");

        // Act
        ChatMessageResponse resp1 = mapper.toResponse(msg1);
        ChatMessageResponse resp2 = mapper.toResponse(msg2);
        ChatMessageResponse resp3 = mapper.toResponse(msg3);

        // Assert
        // Проверяем ID
        assertEquals(msg1.getId(), resp1.getId());
        assertEquals(msg2.getId(), resp2.getId());
        assertEquals(msg3.getId(), resp3.getId());

        // Проверяем содержимое
        assertEquals("First", resp1.getContent());
        assertEquals(ChatMessageType.TEXT, resp1.getType());
        assertFalse(resp1.isUpdated());

        assertEquals(imageId.toString(), resp2.getContent());
        assertEquals(ChatMessageType.IMAGE, resp2.getType());
        assertFalse(resp2.isUpdated());

        assertEquals("Third updated", resp3.getContent());
        assertEquals(ChatMessageType.TEXT, resp3.getType());
        assertEquals(otherParticipant.getId(), resp3.getSenderId());
        assertTrue(resp3.isUpdated());
    }

    @Test
    void shouldMapAllMessagesInChat() {
        // Arrange
        ChatMessage msg1 = chatRoom.addTextMessage(sender, "First");
        ChatMessage msg2 = chatRoom.addTextMessage(sender, "Second");
        ChatMessage msg3 = chatRoom.addTextMessage(otherParticipant, "Third");

        // Act
        var messages = chatRoom.getMessages().stream()
                .map(mapper::toResponse)
                .toList();

        // Assert
        assertEquals(3, messages.size());
        assertEquals(msg1.getId(), messages.get(0).getId());
        assertEquals(msg2.getId(), messages.get(1).getId());
        assertEquals(msg3.getId(), messages.get(2).getId());
        assertEquals("First", messages.get(0).getContent());
        assertEquals("Second", messages.get(1).getContent());
        assertEquals("Third", messages.get(2).getContent());
    }

    @Test
    void shouldMapMessageIdCorrectly() {
        // Arrange
        ChatMessage message = chatRoom.addTextMessage(sender, textContent);
        UUID messageId = message.getId();

        // Act
        ChatMessageResponse response = mapper.toResponse(message);

        // Assert
        assertEquals(messageId, response.getId());
    }

    @Test
    void shouldMapMultipleMessagesWithDifferentSenders() {
        // Arrange
        ChatMessage msg1 = chatRoom.addTextMessage(sender, "From sender");
        ChatMessage msg2 = chatRoom.addTextMessage(otherParticipant, "From other");

        // Act
        ChatMessageResponse resp1 = mapper.toResponse(msg1);
        ChatMessageResponse resp2 = mapper.toResponse(msg2);

        // Assert
        assertEquals(msg1.getId(), resp1.getId());
        assertEquals(msg2.getId(), resp2.getId());
        assertEquals(otherParticipant.getId(), resp2.getSenderId());
        assertEquals("From sender", resp1.getContent());
        assertEquals("From other", resp2.getContent());
    }

    @Test
    void shouldMaintainMessageOrder() {
        // Arrange
        ChatMessage msg1 = chatRoom.addTextMessage(sender, "First");
        ChatMessage msg2 = chatRoom.addTextMessage(sender, "Second");
        ChatMessage msg3 = chatRoom.addTextMessage(sender, "Third");

        // Act
        var responses = chatRoom.getMessages().stream()
                .map(mapper::toResponse)
                .toList();

        // Assert
        assertEquals(3, responses.size());
        assertEquals(msg1.getId(), responses.get(0).getId());
        assertEquals(msg2.getId(), responses.get(1).getId());
        assertEquals(msg3.getId(), responses.get(2).getId());
        assertEquals("First", responses.get(0).getContent());
        assertEquals("Second", responses.get(1).getContent());
        assertEquals("Third", responses.get(2).getContent());
    }

    private Profile createProfile(UUID userId, String name) {
        User user = User.createUser(userId, userId + "@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Profile.createProfile(ownerInfo, name);
    }

}