package com.example.chat.unit.application.service.message.text;

import com.example.chat.application.data.response.ChatMessageResponse;
import com.example.chat.application.exception.AccessDeniedException;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.mapper.ChatMessageMapper;
import com.example.chat.application.service.message.text.TextChatMessageServiceImpl;
import com.example.chat.application.service.message.text.data.request.UpdateTextChatMessageRequest;
import com.example.chat.application.service.message.text.data.response.UpdateTextChatMessageResponse;
import com.example.chat.domain.entity.ChatMessage;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.entity.base.user.User;
import com.example.chat.infrastructure.repository.ChatMessageRepository;
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
public class UpdateTextMessageTests {

    @Mock
    private ChatMessageRepository messageRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private ChatMessageMapper messageMapper;

    @Mock
    private ChatMessage message;

    @InjectMocks
    private TextChatMessageServiceImpl textMessageService;

    private UUID currentUserId;
    private UUID profileId;
    private UUID messageId;
    private String newText;
    private Profile profile;
    private ChatMessageResponse mockResponse;
    private UpdateTextChatMessageRequest request;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        messageId = UUID.randomUUID();
        newText = "Updated text message";

        profile = createProfile(profileId, currentUserId, "Profile");
        mockResponse = mock(ChatMessageResponse.class);

        request = UpdateTextChatMessageRequest.builder()
                .messageId(messageId)
                .newText(newText)
                .build();

        when(securityService.getCurrentUserIdAsUUID()).thenReturn(currentUserId);
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId)).thenReturn(Optional.of(profile));
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(message.isOwner(profile)).thenReturn(true);
        when(message.isText()).thenReturn(true);
        when(message.update(newText)).thenReturn(message);
        when(messageRepository.save(message)).thenReturn(message);
        when(messageMapper.toResponse(message)).thenReturn(mockResponse);
    }

    @Test
    void shouldUpdateTextMessageSuccessfully() {
        // Act
        UpdateTextChatMessageResponse response = textMessageService.updateTextMessage(request);

        // Assert
        assertNotNull(response);
        assertEquals(mockResponse, response.getChatMessageResponse());

        verify(profileRepository).findByOwnerInfoOwnerId(currentUserId);
        verify(messageRepository).findById(messageId);
        verify(message).isOwner(profile);
        verify(message).update(newText);
        verify(messageRepository).save(message);
        verify(messageMapper).toResponse(message);
    }

    @Test
    void shouldThrowWhenProfileNotFound() {
        // Arrange
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> textMessageService.updateTextMessage(request));
    }

    @Test
    void shouldThrowWhenMessageNotFound() {
        // Arrange
        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> textMessageService.updateTextMessage(request));
    }

    @Test
    void shouldThrowWhenUserIsNotOwner() {
        // Arrange
        when(message.isOwner(profile)).thenReturn(false);

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> textMessageService.updateTextMessage(request));
    }

    @Test
    void shouldThrowWhenMessageIsNotText() {
        // Arrange
        when(message.update(any(String.class)))
                .thenThrow(IllegalStateException.class);

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> textMessageService.updateTextMessage(request));
    }

    @Test
    void shouldReturnCorrectResponse() {
        // Act
        UpdateTextChatMessageResponse response = textMessageService.updateTextMessage(request);

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