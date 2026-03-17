package com.example.chat.unit.application.service.chatroom.service;

import com.example.chat.application.exception.AccessDeniedException;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.service.chatroom.ChatRoomServiceImpl;
import com.example.chat.application.service.chatroom.data.request.SetLastReadMessageRequest;
import com.example.chat.domain.entity.ChatMessage;
import com.example.chat.domain.entity.ChatParticipant;
import com.example.chat.domain.entity.ChatRoom;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.entity.base.user.User;
import com.example.chat.infrastructure.repository.ChatMessageRepository;
import com.example.chat.infrastructure.repository.ChatParticipantRepository;
import com.example.chat.infrastructure.repository.ProfileRepository;
import com.example.chat.infrastructure.security.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SetLastReadMessageTests {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatParticipantRepository chatParticipantRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ChatRoomServiceImpl chatRoomService;

    @Captor
    private ArgumentCaptor<ChatParticipant> participantCaptor;

    private UUID currentUserId;
    private UUID profileId;
    private UUID messageId;
    private UUID chatRoomId;
    private Profile profile;
    private ChatRoom chatRoom;
    private ChatMessage targetMessage;
    private ChatParticipant participant;
    private SetLastReadMessageRequest request;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        messageId = UUID.randomUUID();
        chatRoomId = UUID.randomUUID();

        profile = createProfile(profileId, currentUserId, "Test User");
        chatRoom = mock(ChatRoom.class);
        targetMessage = mock(ChatMessage.class);
        participant = mock(ChatParticipant.class);

        request = SetLastReadMessageRequest.builder()
                .messageId(messageId)
                .build();

        when(securityService.getCurrentUserIdAsUUID()).thenReturn(currentUserId);
        when(chatParticipantRepository.findByChatRoomIdAndProfileId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(participant));
        when(chatMessageRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(targetMessage));
        when(targetMessage.getChatRoom())
                .thenReturn(chatRoom);
        when(chatRoom.getId())
                .thenReturn(chatRoomId);
        when(chatRoom.isParticipantByUserId(any(UUID.class)))
                .thenReturn(true);
        when(profileRepository.findByOwnerInfoOwnerId(any(UUID.class)))
                .thenReturn(Optional.of(profile));
        when(chatParticipantRepository.findByChatRoomIdAndProfileId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(participant));
        when(targetMessage.getCreatedAt())
                .thenReturn(LocalDateTime.now());
        when(targetMessage.getId())
                .thenReturn(messageId);
    }

    @Test
    void shouldSetLastReadMessageSuccessfully() {
        // Arrange
        when(participant.getLastReadMessageId()).thenReturn(null);

        // Act
        var response = chatRoomService.setLastReadMessage(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSucceed());

        verify(participant).markMessageAsRead(messageId);
        verify(chatParticipantRepository).save(participant);
    }

    @Test
    void shouldThrowWhenMessageNotFound() {
        // Arrange
        when(chatMessageRepository.findById(messageId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> chatRoomService.setLastReadMessage(request));
    }

    @Test
    void shouldThrowWhenUserIsNotParticipant() {
        // Arrange
        when(chatRoom.isParticipantByUserId(currentUserId)).thenReturn(false);

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> chatRoomService.setLastReadMessage(request));
    }

    @Test
    void shouldThrowWhenProfileNotFound() {
        // Arrange
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> chatRoomService.setLastReadMessage(request));
    }

    @Test
    void shouldThrowWhenParticipantNotFound() {
        // Arrange
        when(chatParticipantRepository.findByChatRoomIdAndProfileId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> chatRoomService.setLastReadMessage(request));
    }

    @Test
    void shouldThrowWhenSettingOlderMessage() {
        // Arrange
        UUID lastReadId = UUID.randomUUID();
        ChatMessage lastReadMessage = mock(ChatMessage.class);

        when(participant.getLastReadMessageId()).thenReturn(lastReadId);
        when(chatMessageRepository.findById(lastReadId)).thenReturn(Optional.of(lastReadMessage));
        when(targetMessage.isOlderThan(lastReadMessage)).thenReturn(true);


        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> chatRoomService.setLastReadMessage(request));
    }

    @Test
    void shouldAllowSettingNewerMessage() {
        // Arrange
        UUID lastReadId = UUID.randomUUID();
        ChatMessage lastReadMessage = mock(ChatMessage.class);

        when(participant.getLastReadMessageId()).thenReturn(lastReadId);
        when(chatMessageRepository.findById(lastReadId)).thenReturn(Optional.of(lastReadMessage));
        when(targetMessage.isOlderThan(lastReadMessage)).thenReturn(false);

        // Act
        var response = chatRoomService.setLastReadMessage(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSucceed());

        verify(participant).markMessageAsRead(messageId);
        verify(chatParticipantRepository).save(participant);
    }

    @Test
    void shouldAllowSettingWhenNoPreviousLastRead() {
        // Arrange
        when(participant.getLastReadMessageId()).thenReturn(null);

        // Act
        var response = chatRoomService.setLastReadMessage(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSucceed());

        verify(participant).markMessageAsRead(messageId);
        verify(chatParticipantRepository).save(participant);
    }

    @Test
    void shouldAllowSettingWhenLastReadMessageNotFound() {
        // Arrange
        UUID lastReadId = UUID.randomUUID();
        when(participant.getLastReadMessageId()).thenReturn(lastReadId);
        when(chatMessageRepository.findById(lastReadId)).thenReturn(Optional.empty());

        // Act
        var response = chatRoomService.setLastReadMessage(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSucceed());

        verify(participant).markMessageAsRead(messageId);
        verify(chatParticipantRepository).save(participant);
    }

    @Test
    void shouldSaveParticipantWithUpdatedLastRead() {
        // Arrange
        when(participant.getLastReadMessageId()).thenReturn(null);

        // Act
        chatRoomService.setLastReadMessage(request);

        // Assert
        verify(chatParticipantRepository).save(participantCaptor.capture());
        ChatParticipant savedParticipant = participantCaptor.getValue();

        verify(savedParticipant).markMessageAsRead(messageId);
    }


    private Profile createProfile(UUID profileId, UUID userId, String name) {
        User user = User.createUser(userId, userId + "@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        Profile profile = Profile.createProfile(ownerInfo, name);
        return profile;
    }

}