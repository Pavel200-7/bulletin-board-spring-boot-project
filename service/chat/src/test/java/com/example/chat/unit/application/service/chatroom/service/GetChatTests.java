package com.example.chat.unit.application.service.chatroom.service;

import com.example.chat.application.data.response.ChatRoomResponse;
import com.example.chat.application.exception.AccessDeniedException;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.mapper.ChatRoomMapper;
import com.example.chat.application.service.chatroom.ChatRoomServiceImpl;
import com.example.chat.application.service.chatroom.data.request.GetChatRequest;
import com.example.chat.application.service.chatroom.data.request.GetUnreadMessageCountRequest;
import com.example.chat.domain.entity.ChatParticipant;
import com.example.chat.domain.entity.ChatRoom;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.entity.base.user.User;
import com.example.chat.infrastructure.repository.ChatMessageRepository;
import com.example.chat.infrastructure.repository.ChatParticipantRepository;
import com.example.chat.infrastructure.repository.ChatRoomRepository;
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
public class GetChatTests {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatParticipantRepository chatParticipantRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private ChatRoomMapper chatRoomMapper;

    @InjectMocks
    private ChatRoomServiceImpl chatRoomService;

    private UUID currentUserId;
    private UUID chatRoomId;
    private ChatRoom chatRoom;
    private Profile currentProfile;
    private Profile otherProfile;
    private ChatRoomResponse mockResponse;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        chatRoomId = UUID.randomUUID();

        currentProfile = createProfile(currentUserId, "Current User");
        otherProfile = createProfile(UUID.randomUUID(), "Other User");

        currentProfile.addContact(otherProfile);

        chatRoom = currentProfile.getChatParticipants().stream()
                .findFirst()
                .map(ChatParticipant::getChatRoom)
                .orElseThrow(() -> new AssertionError("Chat room should exist"));

        chatRoomId = chatRoom.getId();

        mockResponse = mock(ChatRoomResponse.class);

        // Настраиваем базовые моки
        when(securityService.getCurrentUserIdAsUUID()).thenReturn(currentUserId);
        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(chatRoomMapper.toResponseForTwoPartyRoom(chatRoom, currentUserId)).thenReturn(mockResponse);
    }

    @Test
    void shouldGetChatSuccessfully() {
        // Arrange
        GetChatRequest request = createRequest(chatRoomId);

        // Act
        var response = chatRoomService.getChat(request);

        // Assert
        assertNotNull(response);
        assertEquals(mockResponse, response.getChatRoomResponse());

        verify(chatRoomRepository).findById(chatRoomId);
        verify(chatRoomMapper).toResponseForTwoPartyRoom(chatRoom, currentUserId);
    }

    @Test
    void shouldThrowWhenChatNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(chatRoomRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        GetChatRequest request = createRequest(nonExistentId);


        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> chatRoomService.getChat(request));
    }

    @Test
    void shouldThrowWhenUserIsNotParticipant() {
        // Arrange
        UUID strangerId = UUID.randomUUID();
        when(securityService.getCurrentUserIdAsUUID())
                .thenReturn(strangerId);
        GetChatRequest request = createRequest(chatRoomId);

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> chatRoomService.getChat(request));
    }

    @Test
    void shouldPassCorrectChatIdToRepository() {
        // Arrange
        GetChatRequest request = createRequest(chatRoomId);

        // Act
        chatRoomService.getChat(request);

        // Assert
        verify(chatRoomRepository).findById(chatRoomId);
    }

    @Test
    void shouldPassCorrectParametersToMapper() {
        // Arrange
        GetChatRequest request = createRequest(chatRoomId);

        // Act
        chatRoomService.getChat(request);

        // Assert
        verify(chatRoomMapper).toResponseForTwoPartyRoom(chatRoom, currentUserId);
    }

    private GetChatRequest createRequest(UUID chatRoomId) {
        return GetChatRequest.builder()
                .id(chatRoomId)
                .build();
    }

    private Profile createProfile(UUID userId, String name) {
        User user = User.createUser(userId, userId + "@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Profile.createProfile(ownerInfo, name);
    }

}