package com.example.chat.unit.application.mapper;


import com.example.chat.application.data.response.ChatParticipantResponse;
import com.example.chat.application.mapper.ChatParticipantMapper;
import com.example.chat.domain.entity.ChatParticipant;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.entity.base.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class ChatParticipantMapperTests {

    private ChatParticipantMapper mapper = Mappers.getMapper(ChatParticipantMapper.class);

    private UUID currentUserId;
    private UUID otherUserId;
    private Profile currentProfile;
    private Profile otherProfile;
    private ChatParticipant currentParticipant;
    private ChatParticipant otherParticipant;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();

        currentProfile = createProfile(currentUserId, "Current User");
        otherProfile = createProfile(otherUserId, "Other User");

        currentProfile.addContact(otherProfile);

        var chatRoom = currentProfile.getChatParticipants().stream()
                .findFirst()
                .map(p -> p.getChatRoom())
                .orElseThrow(() -> new AssertionError("Chat room should exist"));

        currentParticipant = chatRoom.getParticipants().stream()
                .filter(p -> p.getProfile().getId().equals(currentProfile.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Current participant not found"));

        otherParticipant = chatRoom.getParticipants().stream()
                .filter(p -> p.getProfile().getId().equals(otherProfile.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Other participant not found"));
    }

    @Test
    void shouldMapAllFieldsForCurrentParticipant() {
        // Act
        ChatParticipantResponse response = mapper.toResponse(currentParticipant);

        // Assert
        assertNotNull(response);
        assertEquals(currentParticipant.getId(), response.getId());
        assertEquals(currentProfile.getId(), response.getProfileId());
        assertTrue(response.isOwner());
        assertEquals(currentParticipant.getChatRoom().getId(), response.getChatRoomId());
        assertNull(response.getLastReadMessageId());
    }

    @Test
    void shouldMapAllFieldsForOtherParticipant() {
        // Act
        ChatParticipantResponse response = mapper.toResponse(otherParticipant);

        // Assert
        assertNotNull(response);
        assertEquals(otherParticipant.getId(), response.getId());
        assertEquals(otherProfile.getId(), response.getProfileId());
        assertFalse(response.isOwner()); // Не создатель - не owner
        assertEquals(otherParticipant.getChatRoom().getId(), response.getChatRoomId());
        assertNull(response.getLastReadMessageId());
    }

    @Test
    void shouldMapWithLastReadMessageId() {
        // Arrange
        UUID messageId = UUID.randomUUID();
        currentParticipant.markMessageAsRead(messageId);

        // Act
        ChatParticipantResponse response = mapper.toResponse(currentParticipant);

        // Assert
        assertEquals(messageId, response.getLastReadMessageId());
    }

    @Test
    void shouldMapWithBothLastReadAndHidden() {
        // Arrange
        UUID messageId = UUID.randomUUID();
        currentParticipant.markMessageAsRead(messageId);
        currentParticipant.hide();

        // Act
        ChatParticipantResponse response = mapper.toResponse(currentParticipant);

        // Assert
        assertEquals(messageId, response.getLastReadMessageId());
        assertTrue(response.isOwner());
    }

    @Test
    void shouldMapUnreadAndVisibleParticipant() {
        // Arrange
        otherParticipant.unhide();

        // Act
        ChatParticipantResponse response = mapper.toResponse(otherParticipant);

        // Assert
        assertNull(response.getLastReadMessageId());
        assertFalse(response.isOwner());
    }

    @Test
    void shouldMapMultipleParticipantsFromSameChat() {
        // Act
        ChatParticipantResponse currentResponse = mapper.toResponse(currentParticipant);
        ChatParticipantResponse otherResponse = mapper.toResponse(otherParticipant);

        // Assert
        assertEquals(currentParticipant.getChatRoom().getId(), currentResponse.getChatRoomId());
        assertEquals(otherParticipant.getChatRoom().getId(), otherResponse.getChatRoomId());

        assertNotEquals(currentResponse.getId(), otherResponse.getId());
        assertNotEquals(currentResponse.getProfileId(), otherResponse.getProfileId());
        assertNotEquals(currentResponse.isOwner(), otherResponse.isOwner());
    }

    @Test
    void shouldMapParticipantFromDifferentChat() {
        // Arrange
        Profile thirdProfile = createProfile(UUID.randomUUID(), "Third User");
        currentProfile.addContact(thirdProfile);

        var newChatRoom = currentProfile.getChatParticipants().stream()
                .map(p -> p.getChatRoom())
                .filter(room -> room.getParticipants().stream()
                        .anyMatch(p -> p.getProfile().getId().equals(thirdProfile.getId())))
                .findFirst()
                .orElseThrow(() -> new AssertionError("New chat room should exist"));

        ChatParticipant thirdParticipant = newChatRoom.getParticipants().stream()
                .filter(p -> p.getProfile().getId().equals(thirdProfile.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Third participant not found"));

        // Act
        ChatParticipantResponse response = mapper.toResponse(thirdParticipant);

        // Assert
        assertEquals(thirdProfile.getId(), response.getProfileId());
        assertEquals(newChatRoom.getId(), response.getChatRoomId());
        assertFalse(response.isOwner());
    }

    @Test
    void shouldMaintainImmutability() {
        // Act
        ChatParticipantResponse response = mapper.toResponse(currentParticipant);
        assertNull(currentParticipant.getLastReadMessageId());
        assertFalse(currentParticipant.isHidden());
    }

    private Profile createProfile(UUID userId, String name) {
        User user = User.createUser(userId, userId + "@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Profile.createProfile(ownerInfo, name);
    }

}