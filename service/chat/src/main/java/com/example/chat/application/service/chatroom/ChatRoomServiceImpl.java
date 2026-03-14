package com.example.chat.application.service.chatroom;

import com.example.chat.application.data.response.ChatMessageResponse;
import com.example.chat.application.data.response.ChatRoomResponse;
import com.example.chat.application.exception.AccessDeniedException;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.mapper.ChatMessageMapper;
import com.example.chat.application.mapper.ChatRoomMapper;
import com.example.chat.application.service.chatroom.data.request.GetChatRequest;
import com.example.chat.application.service.chatroom.data.request.GetMessagePaginationRequest;
import com.example.chat.application.service.chatroom.data.request.GetUnreadMessageCountRequest;
import com.example.chat.application.service.chatroom.data.response.GetChatResponse;
import com.example.chat.application.service.chatroom.data.response.GetMessagePaginationResponse;
import com.example.chat.application.service.chatroom.data.response.GetUnreadMessageCountResponse;
import com.example.chat.application.service.chatroom.helper.specification.ChatMessageSpecificationBuilder;
import com.example.chat.application.service.chatroom.helper.specification.data.MessageCursorCriteria;
import com.example.chat.domain.entity.ChatMessage;
import com.example.chat.domain.entity.ChatParticipant;
import com.example.chat.domain.entity.ChatRoom;
import com.example.chat.infrastructure.repository.ChatMessageRepository;
import com.example.chat.infrastructure.repository.ChatParticipantRepository;
import com.example.chat.infrastructure.repository.ChatRoomRepository;
import com.example.chat.infrastructure.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SecurityService securityService;
    private final ChatRoomMapper chatRoomMapper;
    private final ChatMessageMapper chatMessageMapper;
    private final ChatMessageSpecificationBuilder specificationBuilder;

    @Override
    @Transactional(readOnly = true)
    public GetChatResponse getChat(GetChatRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        ChatRoom chatRoom = chatRoomRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + request.getId()));
        if (!chatRoom.isParticipantByUserId(currentUserId)) {
            throw new AccessDeniedException("You are not a participant of this chat");
        }

        ChatRoomResponse response = chatRoomMapper.toResponseForTwoPartyRoom(chatRoom, currentUserId);
        return new GetChatResponse(response);
    }

    @Override
    @Transactional(readOnly = true)
    public GetUnreadMessageCountResponse getUnreadMessageCount(GetUnreadMessageCountRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + request.getChatId()));
        if (!chatRoom.isParticipantByUserId(currentUserId)) {
            throw new AccessDeniedException("You are not a participant of this chat");
        }

        int unreadCount = chatMessageRepository.countUnreadMessages(request.getChatId(), currentUserId);

        log.info("Найдено {} непрочитанных сообщений в чате с id {} у пользователя с id {}",
                unreadCount, request.getChatId(), currentUserId);
        return new GetUnreadMessageCountResponse(unreadCount);
    }

    @Override
    @Transactional(readOnly = true)
    public GetMessagePaginationResponse getMessagePagination(GetMessagePaginationRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + request.getChatId()));

        if (!chatRoom.isParticipantByUserId(currentUserId)) {
            throw new AccessDeniedException("You are not a participant of this chat");
        }

        LocalDateTime cursorTime = null;
        if (request.getCursorMessageId() != null) {
            cursorTime = chatMessageRepository.findById(request.getCursorMessageId())
                    .map(ChatMessage::getCreatedAt)
                    .orElseThrow(() -> new ResourceNotFoundException("Cursor message not found with id: " + request.getCursorMessageId()));
        }

        MessageCursorCriteria criteria = MessageCursorCriteria.builder()
                .chatRoomId(request.getChatId())
                .createdAt(cursorTime)
                .direction(request.getDirection())
                .build();

        Specification<ChatMessage> spec = specificationBuilder.fromCursorCriteria(criteria);
        Pageable pageable = PageRequest.of(0, request.getSize());
        Page<ChatMessage> messages = chatMessageRepository.findAll(spec, pageable);

        Page<ChatMessageResponse> responsePage = messages.map(chatMessageMapper::toResponse);

        log.info("Найдено {} сообщений.", responsePage.getNumberOfElements());
        return new GetMessagePaginationResponse(responsePage);
    }

}
