package com.example.chat.application.service.chatroom;

import com.example.chat.application.data.response.ChatMessageResponse;
import com.example.chat.application.data.response.ChatRoomResponse;
import com.example.chat.application.exception.AccessDeniedException;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.mapper.ChatMessageMapper;
import com.example.chat.application.mapper.ChatRoomMapper;
import com.example.chat.application.service.chatroom.data.request.*;
import com.example.chat.application.service.chatroom.data.response.*;
import com.example.chat.application.service.chatroom.helper.event.MessagesReadEvent;
import com.example.chat.application.service.chatroom.helper.specification.ChatMessageSpecificationBuilder;
import com.example.chat.application.service.chatroom.helper.specification.data.MessageCursorCriteria;
import com.example.chat.domain.entity.ChatMessage;
import com.example.chat.domain.entity.ChatParticipant;
import com.example.chat.domain.entity.ChatRoom;
import com.example.chat.domain.entity.Profile;
import com.example.chat.infrastructure.repository.ChatMessageRepository;
import com.example.chat.infrastructure.repository.ChatParticipantRepository;
import com.example.chat.infrastructure.repository.ChatRoomRepository;
import com.example.chat.infrastructure.repository.ProfileRepository;
import com.example.chat.infrastructure.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ProfileRepository profileRepository;
    private final SecurityService securityService;
    private final ChatRoomMapper chatRoomMapper;
    private final ChatMessageMapper chatMessageMapper;
    private final ChatMessageSpecificationBuilder specificationBuilder;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    public GetChatResponse getChat(GetChatRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        ChatRoom chatRoom = chatRoomRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + request.getId()));
        checkIfCurrentUserIsChatParticipant(chatRoom, currentUserId);

        ChatRoomResponse response = chatRoomMapper.toResponseForTwoPartyRoom(chatRoom, currentUserId);
        return new GetChatResponse(response);
    }

    @Override
    @Transactional(readOnly = true)
    public GetUnreadMessageCountResponse getUnreadMessageCount(GetUnreadMessageCountRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        Profile profile = profileRepository.findByOwnerInfoOwnerId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + currentUserId));
        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + request.getChatId()));
        checkIfCurrentUserIsChatParticipant(chatRoom, currentUserId);

        int unreadCount = chatMessageRepository.countUnreadMessages(request.getChatId(), profile.getId());

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
        checkIfCurrentUserIsChatParticipant(chatRoom, currentUserId);
        Optional<ChatMessage>  cursorMessage = chatMessageRepository.findById(request.getCursorMessageId());

        MessageCursorCriteria criteria;
        if (cursorMessage.isPresent()) {
            criteria = MessageCursorCriteria.fromChatMessage(request.getChatId(), cursorMessage.get(), request.getDirection());
        } else {
            criteria = MessageCursorCriteria.firstPage(request.getChatId());
        }

        Specification<ChatMessage> spec = specificationBuilder.fromCursorCriteria(criteria);
        Pageable pageable = PageRequest.of(0, request.getSize());

        Page<ChatMessage> messages = chatMessageRepository.findAll(spec, pageable);
        Page<ChatMessageResponse> responsePage = toResponsePage(messages, criteria.getDirection());

        log.info("Найдено {} сообщений.", responsePage.getNumberOfElements());
        return new GetMessagePaginationResponse(responsePage);
    }

    private Page<ChatMessageResponse> toResponsePage(Page<ChatMessage> page, Direction direction) {
        List<ChatMessageResponse> messageResponses = new ArrayList<>(page.getContent().stream()
                .map(chatMessageMapper::toResponse)
                .toList());

        if (direction == Direction.DESC) {
            Collections.reverse(messageResponses);
        }

        return new PageImpl<>(
                messageResponses,
                page.getPageable(),
                page.getTotalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public GetMessagePaginationResponse getFirstMessagePage(GetFirstMessagePageRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + request.getChatId()));
        checkIfCurrentUserIsChatParticipant(chatRoom, currentUserId);

        MessageCursorCriteria criteria = MessageCursorCriteria.firstPage(request.getChatId());
        Specification<ChatMessage> spec = specificationBuilder.fromCursorCriteria(criteria);
        Pageable pageable = PageRequest.of(0, request.getSize());

        Page<ChatMessage> messages = chatMessageRepository.findAll(spec, pageable);

        Page<ChatMessageResponse> responsePage = messages.map(chatMessageMapper::toResponse);
        log.info("Найдено {} сообщений на первой странице чата {}.", responsePage.getNumberOfElements(), request.getChatId());
        return new GetMessagePaginationResponse(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public GetMessagePaginationResponse getMessagesAroundLastRead(GetMessagesAroundLastReadRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + request.getChatId()));
        checkIfCurrentUserIsChatParticipant(chatRoom, currentUserId);

        Profile profile = profileRepository.findByOwnerInfoOwnerId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + currentUserId));
        ChatParticipant participant = chatParticipantRepository
                .findByChatRoomIdAndProfileId(chatRoom.getId(), profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found in chat"));

        UUID lastReadMessageId = participant.getLastReadMessageId();

        if (lastReadMessageId == null) {
            MessageCursorCriteria criteria = MessageCursorCriteria.firstPage(request.getChatId());
            Specification<ChatMessage> spec = specificationBuilder.fromCursorCriteria(criteria);
            Pageable pageable = PageRequest.of(0, request.getSize());
            Page<ChatMessage> messages = chatMessageRepository.findAll(spec, pageable);
            Page<ChatMessageResponse> responsePage = messages.map(chatMessageMapper::toResponse);
            log.info("У пользователя нет последнего прочитанного сообщения. Возвращена первая страница чата {}.", request.getChatId());
            return new GetMessagePaginationResponse(responsePage);
        }

        ChatMessage lastReadMessage = chatMessageRepository.findById(lastReadMessageId)
                .orElseThrow(() -> new ResourceNotFoundException("Last read message not found with id: " + lastReadMessageId));

        MessageCursorCriteria criteria = MessageCursorCriteria.fromChatMessage(request.getChatId(), lastReadMessage, Direction.DESC);
        Specification<ChatMessage> spec = specificationBuilder.fromCursorCriteria(criteria);
        Pageable pageable = PageRequest.of(0, request.getSize());

        Page<ChatMessage> messages = chatMessageRepository.findAll(spec, pageable);

        Page<ChatMessageResponse> responsePage = toResponsePage(messages, criteria.getDirection());

        log.info("Найдено {} сообщений вокруг последнего прочитанного сообщения {} в чате {}.",
                responsePage.getNumberOfElements(), lastReadMessageId, request.getChatId());
        return new GetMessagePaginationResponse(responsePage);
    }

    @Override
    @Transactional
    public SetLastReadMessageResponse setLastReadMessage(SetLastReadMessageRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        ChatMessage targetMessage = chatMessageRepository.findById(request.getMessageId())
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + request.getMessageId()));
        ChatRoom chatRoom = targetMessage.getChatRoom();
        checkIfCurrentUserIsChatParticipant(chatRoom, currentUserId);

        Profile profile = profileRepository.findByOwnerInfoOwnerId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + currentUserId));
        ChatParticipant participant = chatParticipantRepository
                .findByChatRoomIdAndProfileId(chatRoom.getId(), profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found in chat"));

        if (isOlderThanLastReadMessage(participant, targetMessage)) {
            throw new IllegalStateException("Cannot set older message as last read");
        }

        participant.markMessageAsRead(targetMessage.getId());
        chatParticipantRepository.save(participant);

        MessagesReadEvent event = MessagesReadEvent.builder()
                .chatRoomId(chatRoom.getId())
                .readerProfileId(currentUserId)
                .readerProfileId(profile.getId())
                .lastReadMessageId(targetMessage.getId())
                .lastReadTimestamp(targetMessage.getCreatedAt())
                .build();
        eventPublisher.publishEvent(event);

        log.info("User {} выставил последнее прочитанное сообщение {} в чате {}",
                currentUserId, targetMessage.getId(), chatRoom.getId());
        return new SetLastReadMessageResponse();
    }

    private void checkIfCurrentUserIsChatParticipant(ChatRoom chatRoom, UUID currentUserId) {
        if (!chatRoom.isParticipantByUserId(currentUserId)) {
            throw new AccessDeniedException("You are not a participant of this chat");
        }
    }

    private boolean isOlderThanLastReadMessage(ChatParticipant participant, ChatMessage targetMessage) {
        UUID lastReadMessageId = participant.getLastReadMessageId();
        if (lastReadMessageId == null) {
            return false;
        }

        return chatMessageRepository.findById(lastReadMessageId)
                .map(lastReadMessage -> targetMessage.isOlderThan(lastReadMessage))
                .orElse(false);
    }

}
