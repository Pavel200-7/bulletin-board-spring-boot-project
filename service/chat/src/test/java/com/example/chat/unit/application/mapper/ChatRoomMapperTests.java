package com.example.chat.unit.application.mapper;

import com.example.chat.application.data.response.ChatParticipantResponse;
import com.example.chat.application.data.response.ChatRoomResponse;
import com.example.chat.application.mapper.ChatParticipantMapper;
import com.example.chat.application.mapper.ChatRoomMapper;
import com.example.chat.application.mapper.ChatRoomMapperImpl;
import com.example.chat.domain.entity.ChatRoom;
import com.example.chat.domain.entity.Contact;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.entity.base.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class ChatRoomMapperTests {

    private ChatRoomMapper mapper;
    private ChatParticipantMapper participantMapper;

    private UUID currentUserId;
    private UUID otherUserId;
    private Profile currentProfile;
    private Profile otherProfile;
    private ChatRoom twoPartyChat;
    private UUID otherProfileImageId;

    @BeforeEach
    void setUp() {
        participantMapper = Mappers.getMapper(ChatParticipantMapper.class);
        mapper = new ChatRoomMapperImpl(participantMapper);

        currentUserId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();
        otherProfileImageId = UUID.randomUUID();

        currentProfile = createProfile(currentUserId, "Current User", null);
        otherProfile = createProfile(otherUserId, "Other User", otherProfileImageId);

        twoPartyChat = createChatRoom(currentProfile, otherProfile);
    }

    @Test
    void shouldMapTwoPartyChatWithImageFromOtherParticipant() {
        // Act
        ChatRoomResponse response = mapper.toResponseForTwoPartyRoom(twoPartyChat, currentUserId);

        // Assert
        assertNotNull(response);
        assertEquals(twoPartyChat.getId(), response.getId());
        assertEquals(twoPartyChat.getName(), response.getName());
        assertEquals(otherProfileImageId, response.getImageId());

        assertNotNull(response.getParticipantResponses());
        assertEquals(2, response.getParticipantResponses().size());
    }

    @Test
    void shouldMapCorrectlyWhenCurrentUserIsNotInChat() {
        // Arrange
        UUID strangerId = UUID.randomUUID();

        // Act
        ChatRoomResponse response = mapper.toResponseForTwoPartyRoom(twoPartyChat, strangerId);

        // Assert
        assertNotNull(response);
        // Если пользователь не в чате, imageId может быть null или первого попавшегося
        // В текущей реализации вернет null, так как не найдет другого участника
        assertNull(response.getImageId());
    }

    @Test
    void shouldMapParticipantsCorrectly() {
        // Act
        ChatRoomResponse response = mapper.toResponseForTwoPartyRoom(twoPartyChat, currentUserId);

        // Assert
        List<ChatParticipantResponse> participants = response.getParticipantResponses();

        // Находим current participant и other participant
        ChatParticipantResponse currentParticipant = participants.stream()
                .filter(p -> p.getProfileId().equals(currentProfile.getId()))
                .findFirst()
                .orElseThrow();

        ChatParticipantResponse otherParticipant = participants.stream()
                .filter(p -> p.getProfileId().equals(otherProfile.getId()))
                .findFirst()
                .orElseThrow();

        assertTrue(currentParticipant.isOwner());
        assertFalse(otherParticipant.isOwner());
        assertEquals(twoPartyChat.getId(), currentParticipant.getChatRoomId());
        assertEquals(twoPartyChat.getId(), otherParticipant.getChatRoomId());
    }

    @Test
    void shouldMapParticipantLastReadMessageId() {
        // Act
        ChatRoomResponse response = mapper.toResponseForTwoPartyRoom(twoPartyChat, currentUserId);

        // Assert
        List<ChatParticipantResponse> participants = response.getParticipantResponses();

        // lastReadMessageId должен быть null по умолчанию
        participants.forEach(p -> assertNull(p.getLastReadMessageId()));
    }

    private Profile createProfile(UUID userId, String name, UUID imageId) {
        User user = User.createUser(userId, userId + "@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        Profile profile = Profile.createProfile(ownerInfo, name);
        if (imageId != null) {
            profile.changeImage(imageId);
        }
        return profile;
    }

    private ChatRoom createChatRoom(Profile owner, Profile otherProfile) {
        Contact contact = currentProfile.addContact(otherProfile);
        return currentProfile.addChatRoom(contact);
    }

}