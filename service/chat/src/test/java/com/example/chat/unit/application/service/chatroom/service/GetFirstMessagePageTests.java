package com.example.chat.unit.application.service.chatroom.service;

import com.example.chat.application.data.response.ChatMessageResponse;
import com.example.chat.application.exception.AccessDeniedException;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.mapper.ChatMessageMapper;
import com.example.chat.application.service.chatroom.ChatRoomServiceImpl;
import com.example.chat.application.service.chatroom.data.request.GetFirstMessagePageRequest;
import com.example.chat.application.service.chatroom.helper.specification.ChatMessageSpecificationBuilder;
import com.example.chat.application.service.chatroom.helper.specification.data.MessageCursorCriteria;
import com.example.chat.domain.entity.ChatMessage;
import com.example.chat.domain.entity.ChatRoom;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.entity.base.user.User;
import com.example.chat.infrastructure.repository.ChatMessageRepository;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GetFirstMessagePageTests {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private ChatMessageMapper chatMessageMapper;

    @Mock
    private ChatMessageSpecificationBuilder specificationBuilder;

    @Mock
    private ChatRoom chatRoom;

    @Mock
    private Specification<ChatMessage> mockSpecification;

    @InjectMocks
    private ChatRoomServiceImpl chatRoomService;

    @Captor
    private ArgumentCaptor<MessageCursorCriteria> criteriaCaptor;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    private UUID currentUserId;
    private UUID chatRoomId;
    private int pageSize;
    private Page<ChatMessage> messagePage;
    private ChatMessageResponse mockResponse;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        chatRoomId = UUID.randomUUID();
        pageSize = 20;

        when(securityService.getCurrentUserIdAsUUID()).thenReturn(currentUserId);
        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(chatRoom.isParticipantByUserId(currentUserId)).thenReturn(true);

        List<ChatMessage> messages = List.of(mock(ChatMessage.class), mock(ChatMessage.class));
        messagePage = new PageImpl<>(messages);

        mockResponse = mock(ChatMessageResponse.class);
        mockSpecification = mock(Specification.class);

        when(specificationBuilder.fromCursorCriteria(any(MessageCursorCriteria.class)))
                .thenReturn(mockSpecification);
        when(chatMessageRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(messagePage);
        when(chatMessageMapper.toResponse(any(ChatMessage.class))).thenReturn(mockResponse);
    }

    @Test
    void shouldGetFirstMessagePageSuccessfully() {
        // Arrange
        GetFirstMessagePageRequest request = GetFirstMessagePageRequest.builder()
                .chatId(chatRoomId)
                .size(pageSize)
                .build();

        // Act
        var response = chatRoomService.getFirstMessagePage(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getChatMessagePage());
        assertEquals(2, response.getChatMessagePage().getContent().size());

        verify(chatRoomRepository).findById(chatRoomId);
        verify(chatRoom).isParticipantByUserId(currentUserId);
        verify(specificationBuilder).fromCursorCriteria(criteriaCaptor.capture());
        verify(chatMessageRepository).findAll(eq(mockSpecification), any(Pageable.class));
        verify(chatMessageMapper, times(2)).toResponse(any(ChatMessage.class));

        MessageCursorCriteria capturedCriteria = criteriaCaptor.getValue();
        assertEquals(chatRoomId, capturedCriteria.getChatRoomId());
    }

    @Test
    void shouldCreatePageableWithCorrectSize() {
        // Arrange
        int customSize = 15;
        GetFirstMessagePageRequest request = GetFirstMessagePageRequest.builder()
                .chatId(chatRoomId)
                .size(customSize)
                .build();

        // Act
        chatRoomService.getFirstMessagePage(request);

        // Assert
        verify(chatMessageRepository).findAll(any(Specification.class), pageableCaptor.capture());
        Pageable capturedPageable = pageableCaptor.getValue();

        assertEquals(0, capturedPageable.getPageNumber());
        assertEquals(customSize, capturedPageable.getPageSize());
    }

    @Test
    void shouldThrowWhenChatNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(chatRoomRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        GetFirstMessagePageRequest request = GetFirstMessagePageRequest.builder()
                .chatId(nonExistentId)
                .size(pageSize)
                .build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> chatRoomService.getFirstMessagePage(request));
    }

    @Test
    void shouldThrowWhenUserIsNotParticipant() {
        // Arrange
        when(chatRoom.isParticipantByUserId(currentUserId)).thenReturn(false);

        GetFirstMessagePageRequest request = GetFirstMessagePageRequest.builder()
                .chatId(chatRoomId)
                .size(pageSize)
                .build();

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> chatRoomService.getFirstMessagePage(request));
    }

    @Test
    void shouldReturnEmptyPageWhenNoMessages() {
        // Arrange
        Page<ChatMessage> emptyPage = Page.empty();
        when(chatMessageRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        GetFirstMessagePageRequest request = GetFirstMessagePageRequest.builder()
                .chatId(chatRoomId)
                .size(pageSize)
                .build();

        // Act
        var response = chatRoomService.getFirstMessagePage(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.getChatMessagePage().isEmpty());
        verify(chatMessageMapper, never()).toResponse(any(ChatMessage.class));
    }

    private Profile createProfile(UUID userId, String name) {
        User user = User.createUser(userId, userId + "@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Profile.createProfile(ownerInfo, name);
    }

}