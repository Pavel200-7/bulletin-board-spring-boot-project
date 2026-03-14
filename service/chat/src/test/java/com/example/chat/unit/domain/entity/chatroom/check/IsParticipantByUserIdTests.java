package com.example.chat.unit.domain.entity.chatroom.check;

import com.example.chat.domain.entity.ChatRoom;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.entity.base.user.User;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ActiveProfiles("test")
public class IsParticipantByUserIdTests {

    private UUID creatorUserId;
    private UUID otherUserId;

    private Profile creator;
    private Profile other;

    private ChatRoom chatRoom;

    @BeforeEach
    public void setup() {
        creatorUserId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();

        Profile creator = createProfile(creatorUserId,"creator@example.com", "Creator");
        Profile other = createProfile(otherUserId, "other@example.com", "Other");

        chatRoom = createChatRoom(creator, other);
    }

    @Test
    public void shouldReturnTrueWhenParticipant() {
        // Act
        boolean result = chatRoom.isParticipantByUserId(creatorUserId);

        // Assert
        assertTrue(result);
    }

    @Test
    public void shouldReturnFalseWhenNotParticipant() {
        // Arrange
        UUID strangerUserId = UUID.randomUUID();

        // Act
        boolean result = chatRoom.isParticipantByUserId(strangerUserId);

        // Assert
        assertFalse(result);
    }

    private ChatRoom createChatRoom(Profile creator, Profile other) {
        creator.addContact(other);
        ChatRoom chatRoom = creator.getChatParticipants().getFirst().getChatRoom();
        return chatRoom;
    }

    private Profile createProfile(UUID userId, String email, String publicName) {
        User user = User.createUser(userId, email);
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Profile.createProfile(ownerInfo, publicName);
    }

}
