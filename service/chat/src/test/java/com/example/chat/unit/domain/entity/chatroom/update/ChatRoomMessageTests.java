package com.example.chat.unit.domain.entity.chatroom.update;

import com.example.chat.domain.entity.ChatMessage;
import com.example.chat.domain.entity.ChatRoom;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.entity.base.user.User;
import com.example.chat.domain.enums.ChatMessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class ChatRoomMessageTests {

    private Profile sender;
    private Profile otherParticipant;
    private Profile nonParticipant;
    private ChatRoom chatRoom;

    @BeforeEach
    void setUp() {
        sender = createProfile("sender@example.com", "Sender");
        otherParticipant = createProfile("other@example.com", "Other");
        nonParticipant = createProfile("non@example.com", "Non Participant");
        sender.addContact(otherParticipant);
        chatRoom = sender.getChatParticipants().getFirst().getChatRoom();
    }

    @Test
    void shouldAddTextMessage() {
        // Arrange
        String text = "Hello, world!";
        int initialSize = chatRoom.getMessages().size();

        // Act
        ChatMessage message = chatRoom.addTextMessage(sender, text);

        // Assert
        assertNotNull(message);
        assertEquals(sender, message.getSender());
        assertEquals(chatRoom, message.getChatRoom());
        assertEquals(ChatMessageType.TEXT, message.getType());
        assertEquals(text, message.getContent());
        assertTrue(chatRoom.getMessages().contains(message));
        assertEquals(initialSize + 1, chatRoom.getMessages().size());
    }

    @Test
    void shouldAddImageMessage() {
        // Arrange
        UUID imageId = UUID.randomUUID();
        int initialSize = chatRoom.getMessages().size();

        // Act
        ChatMessage message = chatRoom.addImageMessage(sender, imageId);

        // Assert
        assertNotNull(message);
        assertEquals(sender, message.getSender());
        assertEquals(chatRoom, message.getChatRoom());
        assertEquals(ChatMessageType.IMAGE, message.getType());
        assertEquals(imageId.toString(), message.getContent());
        assertEquals(imageId, message.getImageId());
        assertTrue(chatRoom.getMessages().contains(message));
        assertEquals(initialSize + 1, chatRoom.getMessages().size());
    }

    @Test
    void shouldAddMultipleMessages() {
        // Arrange
        String text1 = "First message";
        String text2 = "Second message";
        UUID imageId = UUID.randomUUID();

        // Act
        ChatMessage message1 = chatRoom.addTextMessage(sender, text1);
        ChatMessage message2 = chatRoom.addTextMessage(sender, text2);
        ChatMessage message3 = chatRoom.addImageMessage(sender, imageId);

        // Assert
        assertEquals(3, chatRoom.getMessages().size());
        assertTrue(chatRoom.getMessages().contains(message1));
        assertTrue(chatRoom.getMessages().contains(message2));
        assertTrue(chatRoom.getMessages().contains(message3));
    }

    @Test
    void shouldThrowWhenNonParticipantSendsTextMessage() {
        // Arrange
        String text = "Hello";

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                chatRoom.addTextMessage(nonParticipant, text));
    }

    @Test
    void shouldThrowWhenNonParticipantSendsImageMessage() {
        // Arrange
        UUID imageId = UUID.randomUUID();

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                chatRoom.addImageMessage(nonParticipant, imageId));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    void shouldThrowWhenTextMessageContentIsInvalid(String invalidText) {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                chatRoom.addTextMessage(sender, invalidText));

        assertEquals("Message text cannot be empty", exception.getMessage());
    }

    @Test
    void shouldThrowWhenImageIdIsNull() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                chatRoom.addImageMessage(sender, null));

        assertEquals("Image ID cannot be null", exception.getMessage());
    }


    @Test
    void shouldNotAddTextMessageWhenTextIsNull() {
        // Arrange
        int initialSize = chatRoom.getMessages().size();

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                chatRoom.addTextMessage(sender, null));

        assertEquals("Message text cannot be empty", exception.getMessage());
        assertEquals(initialSize, chatRoom.getMessages().size(), "Message count should not change");
    }

    @Test
    void shouldNotAddTextMessageWhenTextIsEmpty() {
        // Arrange
        int initialSize = chatRoom.getMessages().size();
        String emptyText = "";

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                chatRoom.addTextMessage(sender, emptyText));

        assertEquals("Message text cannot be empty", exception.getMessage());
        assertEquals(initialSize, chatRoom.getMessages().size(), "Message count should not change");
    }

    @Test
    void shouldAllowOtherParticipantToSendMessages() {
        // Arrange
        String text = "Message from other participant";

        // Act
        ChatMessage message = chatRoom.addTextMessage(otherParticipant, text);

        // Assert
        assertNotNull(message);
        assertEquals(otherParticipant, message.getSender());
        assertEquals(text, message.getContent());
    }

    @Test
    void shouldAllowTextMessageWithMaximalLength() {
        // Arrange
        String longText = "a".repeat(10000); // Предполагаем, что БД выдержит

        // Act
        ChatMessage message = chatRoom.addTextMessage(sender, longText);

        // Assert
        assertEquals(longText, message.getContent());
    }

    private Profile createProfile(String email, String publicName) {
        User user = User.createUser(UUID.randomUUID(), email);
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Profile.createProfile(ownerInfo, publicName);
    }

    @Test
    void shouldAllowBothParticipantsToSendMessages() {
        // Arrange
        String text1 = "Message from sender";
        String text2 = "Message from other";

        // Act
        ChatMessage message1 = chatRoom.addTextMessage(sender, text1);
        ChatMessage message2 = chatRoom.addTextMessage(otherParticipant, text2);

        // Assert
        assertEquals(2, chatRoom.getMessages().size());
        assertTrue(chatRoom.getMessages().contains(message1));
        assertTrue(chatRoom.getMessages().contains(message2));
    }

}