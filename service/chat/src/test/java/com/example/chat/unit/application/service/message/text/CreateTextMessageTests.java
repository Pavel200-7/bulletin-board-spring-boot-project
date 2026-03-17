package com.example.chat.unit.application.service.message.text;

import com.example.chat.application.data.response.ChatMessageResponse;
import com.example.chat.application.exception.AccessDeniedException;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.mapper.ChatMessageMapper;
import com.example.chat.application.service.message.text.TextChatMessageServiceImpl;
import com.example.chat.application.service.message.text.data.request.CreateTextChatMessageRequest;
import com.example.chat.application.service.message.text.data.response.CreateTextChatMessageResponse;
import com.example.chat.domain.entity.ChatMessage;
import com.example.chat.domain.entity.ChatRoom;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.entity.base.user.User;
import com.example.chat.infrastructure.repository.ChatMessageRepository;
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
public class CreateTextMessageTests {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatMessageMapper messageMapper;

    @Mock
    private ChatRoom chatRoom;

    @Mock
    private ChatMessage createdMessage;

    @InjectMocks
    private TextChatMessageServiceImpl textMessageService;

    private UUID currentUserId;
    private UUID profileId;
    private UUID chatRoomId;
    private String text;
    private Profile sender;
    private ChatMessageResponse mockResponse;
    private CreateTextChatMessageRequest request;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        chatRoomId = UUID.randomUUID();
        text = "Hello, world!";

        sender = createProfile(profileId, currentUserId, "Sender");
        mockResponse = mock(ChatMessageResponse.class);

        request = CreateTextChatMessageRequest.builder()
                .chatId(chatRoomId)
                .text(text)
                .build();

        when(securityService.getCurrentUserIdAsUUID()).thenReturn(currentUserId);
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId)).thenReturn(Optional.of(sender));
        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(chatRoom.isParticipantByUserId(currentUserId)).thenReturn(true);
        when(chatRoom.addTextMessage(sender, text)).thenReturn(createdMessage);
        when(chatRoomRepository.save(chatRoom)).thenReturn(chatRoom);
        when(messageMapper.toResponse(createdMessage)).thenReturn(mockResponse);
    }

    @Test
    void shouldCreateTextMessageSuccessfully() {
        // Act
        CreateTextChatMessageResponse response = textMessageService.createTextMessage(request);

        // Assert
        assertNotNull(response);
        assertEquals(mockResponse, response.getChatMessageResponse());

        verify(profileRepository).findByOwnerInfoOwnerId(currentUserId);
        verify(chatRoomRepository).findById(chatRoomId);
        verify(chatRoom).addTextMessage(sender, text);
        verify(chatRoomRepository).save(chatRoom);
        verify(messageMapper).toResponse(createdMessage);
    }

    @Test
    void shouldThrowWhenProfileNotFound() {
        // Arrange
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> textMessageService.createTextMessage(request));
    }

    @Test
    void shouldThrowWhenChatRoomNotFound() {
        // Arrange
        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> textMessageService.createTextMessage(request));
    }

    @Test
    void shouldThrowWhenUserIsNotParticipant() {
        // Arrange
        when(chatRoom.isParticipantByUserId(currentUserId)).thenReturn(false);

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> textMessageService.createTextMessage(request));
    }

    @Test
    void shouldReturnCorrectResponse() {
        // Act
        CreateTextChatMessageResponse response = textMessageService.createTextMessage(request);

        // Assert
        assertNotNull(response);
        assertEquals(mockResponse, response.getChatMessageResponse());
    }

    private Profile createProfile(UUID profileId, UUID userId, String name) {
        User user = User.createUser(userId, userId + "@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        Profile profile = Profile.createProfile(ownerInfo, name);
        return profile;
    }

}