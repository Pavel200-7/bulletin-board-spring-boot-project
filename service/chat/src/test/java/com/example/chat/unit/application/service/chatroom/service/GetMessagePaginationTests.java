package com.example.chat.unit.application.service.chatroom.service;

import com.example.chat.application.data.response.ChatMessageResponse;
import com.example.chat.application.exception.AccessDeniedException;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.mapper.ChatMessageMapper;
import com.example.chat.application.service.chatroom.ChatRoomServiceImpl;
import com.example.chat.application.service.chatroom.data.request.GetMessagePaginationRequest;
import com.example.chat.application.service.chatroom.helper.specification.ChatMessageSpecificationBuilder;
import com.example.chat.application.service.chatroom.helper.specification.data.MessageCursorCriteria;
import com.example.chat.domain.entity.ChatMessage;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GetMessagePaginationTests {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatParticipantRepository chatParticipantRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private ChatMessageMapper chatMessageMapper;

    @Mock
    private ChatMessageSpecificationBuilder specificationBuilder;

    @InjectMocks
    private ChatRoomServiceImpl chatRoomService;

    @Captor
    private ArgumentCaptor<MessageCursorCriteria> criteriaCaptor;

    private UUID currentUserId;
    private UUID chatRoomId;
    private UUID cursorMessageId;
    private ChatRoom chatRoom;
    private Profile currentProfile;
    private Profile otherProfile;
    private ChatMessage cursorMessage;
    private Page<ChatMessage> messagePage;
    private ChatMessageResponse mockResponse;
    private Specification<ChatMessage> mockSpecification;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        currentUserId = UUID.randomUUID();
        chatRoomId = UUID.randomUUID();
        cursorMessageId = UUID.randomUUID();

        currentProfile = createProfile(currentUserId, "Current User");
        otherProfile = createProfile(UUID.randomUUID(), "Other User");

        currentProfile.addContact(otherProfile);

        chatRoom = currentProfile.getChatParticipants().stream()
                .findFirst()
                .map(ChatParticipant::getChatRoom)
                .orElseThrow(() -> new AssertionError("Chat room should exist"));

        chatRoomId = chatRoom.getId();

        cursorMessage = mock(ChatMessage.class);
        when(cursorMessage.getCreatedAt()).thenReturn(LocalDateTime.now());

        List<ChatMessage> messages = List.of(mock(ChatMessage.class), mock(ChatMessage.class));
        messagePage = new PageImpl<>(messages);

        mockResponse = mock(ChatMessageResponse.class);
        mockSpecification = mock(Specification.class);

        // Настраиваем базовые моки
        when(securityService.getCurrentUserIdAsUUID()).thenReturn(currentUserId);
        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(chatMessageRepository.findById(cursorMessageId)).thenReturn(Optional.of(cursorMessage));
        when(specificationBuilder.fromCursorCriteria(any(MessageCursorCriteria.class)))
                .thenReturn(mockSpecification);
        when(chatMessageRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(messagePage);
        when(chatMessageMapper.toResponse(any(ChatMessage.class))).thenReturn(mockResponse);
    }

    @Test
    void shouldGetMessagePaginationSuccessfully() {
        // Arrange
        GetMessagePaginationRequest request = createRequest(cursorMessageId);

        // Act
        var response = chatRoomService.getMessagePagination(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getChatMessagePage());
        assertEquals(2, response.getChatMessagePage().getContent().size());

        verify(chatRoomRepository).findById(chatRoomId);
        verify(chatMessageRepository).findById(cursorMessageId);
        verify(specificationBuilder).fromCursorCriteria(any(MessageCursorCriteria.class));
        verify(chatMessageRepository).findAll(eq(mockSpecification), any(Pageable.class));
        verify(chatMessageMapper, times(2)).toResponse(any(ChatMessage.class));
    }

    @Test
    void shouldGetMessagePaginationWithoutCursor() {
        // Arrange
        GetMessagePaginationRequest request = createRequest(null);

        // Act
        chatRoomService.getMessagePagination(request);

        // Assert
        verify(chatMessageRepository, never()).findById(any());
        verify(specificationBuilder).fromCursorCriteria(criteriaCaptor.capture());

        MessageCursorCriteria capturedCriteria = criteriaCaptor.getValue();
        assertEquals(chatRoomId, capturedCriteria.getChatRoomId());
        assertNull(capturedCriteria.getCreatedAt());
        assertEquals(Sort.Direction.DESC, capturedCriteria.getDirection());
    }

    @Test
    void shouldThrowWhenChatNotFound() {
        // Arrange
        when(chatRoomRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());
        GetMessagePaginationRequest request = createRequest(cursorMessageId);


        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> chatRoomService.getMessagePagination(request));

        verify(chatMessageRepository, never()).findById(any());
        verify(specificationBuilder, never()).fromCursorCriteria(any());
    }

    @Test
    void shouldThrowWhenUserIsNotParticipant() {
        // Arrange
        UUID strangerId = UUID.randomUUID();
        when(securityService.getCurrentUserIdAsUUID())
                .thenReturn(strangerId);
        GetMessagePaginationRequest request = createRequest(cursorMessageId);

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> chatRoomService.getMessagePagination(request));
    }

    @Test
    void shouldThrowWhenCursorMessageNotFound() {
        // Arrange
        UUID nonExistentCursorId = UUID.randomUUID();
        when(chatMessageRepository.findById(nonExistentCursorId)).thenReturn(Optional.empty());
        GetMessagePaginationRequest request = createRequest(nonExistentCursorId);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> chatRoomService.getMessagePagination(request));
    }

    @Test
    void shouldPassCorrectCriteriaToBuilder() {
        // Arrange
        GetMessagePaginationRequest request = createRequest(cursorMessageId);

        // Act
        chatRoomService.getMessagePagination(request);

        // Assert
        verify(specificationBuilder).fromCursorCriteria(criteriaCaptor.capture());
        MessageCursorCriteria captured = criteriaCaptor.getValue();

        assertEquals(chatRoomId, captured.getChatRoomId());
        assertEquals(cursorMessage.getCreatedAt(), captured.getCreatedAt());
        assertEquals(Sort.Direction.DESC, captured.getDirection());
    }

    private GetMessagePaginationRequest createRequest(UUID cursorMessageId) {
        return GetMessagePaginationRequest.builder()
                .chatId(chatRoomId)
                .cursorMessageId(cursorMessageId)
                .direction(Sort.Direction.DESC)
                .size(20)
                .build();
    }

    private Profile createProfile(UUID userId, String name) {
        User user = User.createUser(userId, userId + "@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Profile.createProfile(ownerInfo, name);
    }

}