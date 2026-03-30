package com.example.chat.unit.domain.entity.chatmessage.update;

import com.example.chat.domain.entity.ChatMessage;
import com.example.chat.domain.entity.ChatRoom;
import com.example.chat.domain.entity.Contact;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.entity.base.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class SetReadTests {

    private Profile owner;
    private Profile other;
    private ChatMessage message;

    @BeforeEach
    public void setup() {
        owner = createProfile("Owner@.su", "Owner");
        other = createProfile("Other@.su", "Other");

        ChatRoom chat = createChatRoom(owner, other);
        message = chat.addTextMessage(owner, "some text mes");
    }

    @Test
    public void shouldSetRead() {
        // Act
        message.setRead(other);

        // Assert
        assertTrue(message.isRead());
    }

    @Test
    public void shouldThrowWhenOwnerTrySetRead() {
        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> message.setRead(owner));
    }


    private ChatRoom createChatRoom(Profile creator, Profile other) {
        Contact contact = creator.addContact(other);
        ChatRoom chatRoom = creator.addChatRoom(contact);
        return chatRoom;
    }

    private Profile createProfile(String email, String publicName) {
        User user = User.createUser(UUID.randomUUID(), email);
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Profile.createProfile(ownerInfo, publicName);
    }

}
