package com.example.chat.unit.application.service.chatroom.service;

import com.example.chat.application.exception.AccessDeniedException;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.service.chatroom.ChatRoomServiceImpl;
import com.example.chat.application.service.chatroom.data.request.GetMessagesAroundLastReadRequest;
import com.example.chat.application.service.chatroom.data.request.GetUnreadMessageCountRequest;
import com.example.chat.domain.entity.ChatParticipant;
import com.example.chat.domain.entity.ChatRoom;
import com.example.chat.domain.entity.Contact;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.entity.base.user.User;
import com.example.chat.infrastructure.repository.ChatMessageRepository;
import com.example.chat.infrastructure.repository.ChatParticipantRepository;
import com.example.chat.infrastructure.repository.ChatRoomRepository;
import com.example.chat.infrastructure.repository.ProfileRepository;
import com.example.chat.infrastructure.security.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GetUnreadMessageCountTests {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private ChatRoomServiceImpl chatRoomService;

    private UUID currentUserId;
    private UUID chatRoomId;
    private ChatRoom chatRoom;
    private Profile currentProfile;
    private Profile otherProfile;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        chatRoomId = UUID.randomUUID();

        currentProfile = createProfile(currentUserId, "Current User");
        otherProfile = createProfile(UUID.randomUUID(), "Other User");

        Contact contact = currentProfile.addContact(otherProfile);
        ChatRoom chatRoom = currentProfile.addChatRoom(contact);

        chatRoomId = chatRoom.getId();

        when(profileRepository.findByOwnerInfoOwnerId(any(UUID.class))).thenReturn(Optional.of(currentProfile));
        when(securityService.getCurrentUserIdAsUUID()).thenReturn(currentUserId);
        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
    }

    @Test
    void shouldReturnUnreadCountWhenUserIsParticipant() {
        // Arrange
        int expectedCount = 5;
        when(chatMessageRepository.countUnreadMessages(any(UUID.class), any(UUID.class)))
                .thenReturn(expectedCount);
        GetUnreadMessageCountRequest request = createRequest(chatRoomId);


        // Act
        var response = chatRoomService.getUnreadMessageCount(request);

        // Assert
        assertNotNull(response);
        assertEquals(expectedCount, response.getCount());
    }

    @Test
    void shouldReturnZeroWhenNoUnreadMessages() {
        // Arrange
        when(chatMessageRepository.countUnreadMessages(chatRoomId, currentUserId))
                .thenReturn(0);
        GetUnreadMessageCountRequest request = createRequest(chatRoomId);


        // Act
        var response = chatRoomService.getUnreadMessageCount(request);

        // Assert
        assertEquals(0, response.getCount());
    }

    @Test
    void shouldThrowWhenChatNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(chatRoomRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());
        GetUnreadMessageCountRequest request = createRequest(nonExistentId);


        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> chatRoomService.getUnreadMessageCount(request));
    }

    @Test
    void shouldThrowWhenUserIsNotParticipant() {
        // Arrange
        UUID strangerId = UUID.randomUUID();
        when(securityService.getCurrentUserIdAsUUID())
                .thenReturn(strangerId);
        GetUnreadMessageCountRequest request = createRequest(chatRoomId);


        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> chatRoomService.getUnreadMessageCount(request));
    }

    @Test
    void shouldThrowWhenProfileNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.empty());
        GetUnreadMessageCountRequest request = createRequest(nonExistentId);


        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> chatRoomService.getUnreadMessageCount(request));
    }

    private GetUnreadMessageCountRequest createRequest(UUID chatRoomId) {
        return GetUnreadMessageCountRequest.builder()
                .chatId(chatRoomId)
                .build();
    }

    private Profile createProfile(UUID userId, String name) {
        User user = User.createUser(userId, userId + "@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Profile.createProfile(ownerInfo, name);
    }

}