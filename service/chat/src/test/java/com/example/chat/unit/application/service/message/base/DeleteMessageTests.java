package com.example.chat.unit.application.service.message.base;

import com.example.chat.application.exception.AccessDeniedException;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.service.message.base.BaseChatMessageServiceImpl;
import com.example.chat.application.service.message.base.data.request.DeleteChatMessageRequest;
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
public class DeleteMessageTests {

    @Mock
    private ChatMessageRepository messageRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private TestChatMessageServiceImpl messageService;

    private UUID currentUserId;
    private UUID profileId;
    private UUID messageId;
    private Profile currentProfile;
    private ChatMessage message;
    private DeleteChatMessageRequest request;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        messageId = UUID.randomUUID();

        currentProfile = createProfile(profileId, currentUserId, "Current User");
        message = mock(ChatMessage.class);
        request = DeleteChatMessageRequest.builder()
                .messageId(messageId)
                .build();

        when(securityService.getCurrentUserIdAsUUID()).thenReturn(currentUserId);
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId)).thenReturn(Optional.of(currentProfile));
        when(message.isOwner(currentProfile)).thenReturn(true);
    }

    @Test
    void shouldDeleteMessageSuccessfully() {
        // Act
        var response = messageService.deleteMessage(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSucceed());

        verify(messageRepository).findById(messageId);
        verify(profileRepository).findByOwnerInfoOwnerId(currentUserId);
        verify(message).isOwner(currentProfile);
        verify(messageRepository).delete(message);
    }

    @Test
    void shouldThrowWhenMessageNotFound() {
        // Arrange
        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> messageService.deleteMessage(request));
    }

    @Test
    void shouldThrowWhenProfileNotFound() {
        // Arrange
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> messageService.deleteMessage(request));
    }

    @Test
    void shouldThrowWhenUserIsNotOwner() {
        // Arrange
        when(message.isOwner(currentProfile)).thenReturn(false);

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> messageService.deleteMessage(request));
    }

    private Profile createProfile(UUID profileId, UUID userId, String name) {
        User user = User.createUser(userId, userId + "@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        Profile profile = Profile.createProfile(ownerInfo, name);
        return profile;
    }

    private static class TestChatMessageServiceImpl extends BaseChatMessageServiceImpl {
        public TestChatMessageServiceImpl(
                ChatMessageRepository messageRepository,
                ProfileRepository profileRepository,
                SecurityService securityService) {
            super(messageRepository, profileRepository, securityService);
        }
    }

}
