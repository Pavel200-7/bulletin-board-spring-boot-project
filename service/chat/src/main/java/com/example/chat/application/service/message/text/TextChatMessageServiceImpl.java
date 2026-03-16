package com.example.chat.application.service.message.text;

import com.example.chat.application.data.response.ChatMessageResponse;
import com.example.chat.application.exception.AccessDeniedException;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.mapper.ChatMessageMapper;
import com.example.chat.application.service.message.base.BaseChatMessageServiceImpl;
import com.example.chat.application.service.message.text.data.request.CreateTextChatMessageRequest;
import com.example.chat.application.service.message.text.data.request.UpdateTextChatMessageRequest;
import com.example.chat.application.service.message.text.data.response.CreateTextChatMessageResponse;
import com.example.chat.application.service.message.text.data.response.UpdateTextChatMessageResponse;
import com.example.chat.domain.entity.ChatMessage;
import com.example.chat.domain.entity.ChatRoom;
import com.example.chat.domain.entity.Profile;
import com.example.chat.infrastructure.repository.ChatMessageRepository;
import com.example.chat.infrastructure.repository.ChatRoomRepository;
import com.example.chat.infrastructure.repository.ProfileRepository;
import com.example.chat.infrastructure.security.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class TextChatMessageServiceImpl
        extends BaseChatMessageServiceImpl
        implements TextChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageMapper messageMapper;

    public TextChatMessageServiceImpl(
            ChatMessageRepository messageRepository,
            ProfileRepository profileRepository,
            SecurityService securityService,
            ChatRoomRepository chatRoomRepository,
            ChatMessageMapper messageMapper) {
        super(messageRepository, profileRepository, securityService);
        this.chatRoomRepository = chatRoomRepository;
        this.messageMapper = messageMapper;
    }

    @Override
    @Transactional
    public CreateTextChatMessageResponse createTextMessage(CreateTextChatMessageRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        Profile sender = profileRepository.findByOwnerInfoOwnerId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for current user with id: " + currentUserId));
        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + request.getChatId()));

        if (!chatRoom.isParticipantByUserId(currentUserId)) {
            throw new AccessDeniedException("You are not a participant of this chat");
        }

        ChatMessage message = chatRoom.addTextMessage(sender, request.getText());

        messageRepository.save(message);
        log.info("Сообщение с типом 'Текст' создано с id: {}", message.getId());

        ChatMessageResponse messageResponse = messageMapper.toResponse(message);
        return new CreateTextChatMessageResponse(messageResponse);
    }

    @Override
    @Transactional
    public UpdateTextChatMessageResponse updateTextMessage(UpdateTextChatMessageRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        Profile profile = profileRepository.findByOwnerInfoOwnerId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for current user with id: " + currentUserId));
        ChatMessage message = messageRepository.findById(request.getMessageId())
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + request.getMessageId()));

        if (!message.isOwner(profile)) {
            throw new AccessDeniedException("You are not the owner of this message");
        }

        message.update(request.getNewText());

        message = messageRepository.save(message);
        log.info("Успешно обновлено текстовое сообщение с id: {}", message.getId());

        ChatMessageResponse messageResponse = messageMapper.toResponse(message);
        return new UpdateTextChatMessageResponse(messageResponse);
    }

}