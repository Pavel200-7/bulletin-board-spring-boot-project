package com.example.chat.unit.domain.entity.chatmessage.check;

import com.example.chat.domain.entity.ChatMessage;
import com.example.chat.domain.entity.ChatRoom;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.entity.base.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class IsYoungerThanTests {

    private Profile owner;
    private Profile other;
    private ChatMessage messageOlder;
    private ChatMessage messageYounger;

    @BeforeEach
    public void setup() throws Exception {
        owner = createProfile("Owner@.su", "Owner");
        other = createProfile("Other@.su", "Other");

        ChatRoom chat = createChatRoom(owner, other);
        messageOlder = chat.addTextMessage(owner, "some text mes");
        messageYounger = chat.addTextMessage(owner, "some text mes");

        setCreatedAt(messageOlder, LocalDateTime.now().minusMinutes(10));
        setCreatedAt(messageYounger, LocalDateTime.now());
    }

    @Test
    public void shouldReturnTrueWhenYounger() {
        // Act
        boolean result = messageYounger.isYoungerThan(messageOlder);

        // Assert
        assertTrue(result);
    }

    @Test
    public void shouldReturnFalseWhenOlder() {
        // Act
        boolean result = messageOlder.isYoungerThan(messageYounger);

        // Assert
        assertFalse(result);
    }

    private ChatRoom createChatRoom(Profile creator, Profile other) {
        creator.addContact(other);
        ChatRoom chatRoom = creator.getChatParticipants().getFirst().getChatRoom();
        return chatRoom;
    }

    private Profile createProfile(String email, String publicName) {
        User user = User.createUser(UUID.randomUUID(), email);
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Profile.createProfile(ownerInfo, publicName);
    }

    private void setCreatedAt(ChatMessage message, LocalDateTime time) throws Exception {
        Field createdAtField = ChatMessage.class.getSuperclass().getDeclaredField("createdAt");
        createdAtField.setAccessible(true);
        createdAtField.set(message, time);
    }
}
