package com.example.chat.unit.application.service.message.image;

import com.example.chat.application.data.response.ChatMessageResponse;
import com.example.chat.application.exception.AccessDeniedException;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.mapper.ChatMessageMapper;
import com.example.chat.application.service.message.image.ImageChatMessageServiceImpl;
import com.example.chat.application.service.message.image.data.request.CreateImageChatMessageRequest;
import com.example.chat.application.service.message.image.data.response.CreateImageChatMessageResponse;
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
public class CreateImageMessageTests {

    @Mock
    private ChatMessageRepository messageRepository;

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
    private ImageChatMessageServiceImpl imageMessageService;

    private UUID currentUserId;
    private UUID profileId;
    private UUID chatRoomId;
    private UUID imageId;
    private Profile sender;
    private ChatMessageResponse mockResponse;
    private CreateImageChatMessageRequest request;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        chatRoomId = UUID.randomUUID();
        imageId = UUID.randomUUID();

        sender = createProfile(profileId, currentUserId, "Sender");
        mockResponse = mock(ChatMessageResponse.class);

        request = CreateImageChatMessageRequest.builder()
                .chatId(chatRoomId)
                .imageId(imageId)
                .build();

        when(securityService.getCurrentUserIdAsUUID()).thenReturn(currentUserId);
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId)).thenReturn(Optional.of(sender));
        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(chatRoom.isParticipantByUserId(currentUserId)).thenReturn(true);
        when(chatRoom.addImageMessage(sender, imageId)).thenReturn(createdMessage);
        when(messageRepository.save(createdMessage)).thenReturn(createdMessage);
        when(messageMapper.toResponse(createdMessage)).thenReturn(mockResponse);
    }

    @Test
    void shouldCreateImageMessageSuccessfully() {
        // Act
        CreateImageChatMessageResponse response = imageMessageService.createImageMessage(request);

        // Assert
        assertNotNull(response);
        assertEquals(mockResponse, response.getChatMessageResponse());

        verify(profileRepository).findByOwnerInfoOwnerId(currentUserId);
        verify(chatRoomRepository).findById(chatRoomId);
        verify(chatRoom).addImageMessage(sender, imageId);
        verify(messageRepository).save(createdMessage);
        verify(messageMapper).toResponse(createdMessage);
    }

    @Test
    void shouldThrowWhenProfileNotFound() {
        // Arrange
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> imageMessageService.createImageMessage(request));
    }

    @Test
    void shouldThrowWhenChatRoomNotFound() {
        // Arrange
        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> imageMessageService.createImageMessage(request));
    }

    @Test
    void shouldThrowWhenUserIsNotParticipant() {
        // Arrange
        when(chatRoom.isParticipantByUserId(currentUserId)).thenReturn(false);

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> imageMessageService.createImageMessage(request));
    }

    @Test
    void shouldReturnCorrectResponse() {
        // Act
        CreateImageChatMessageResponse response = imageMessageService.createImageMessage(request);

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