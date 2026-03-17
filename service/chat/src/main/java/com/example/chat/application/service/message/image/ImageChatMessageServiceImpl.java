package com.example.chat.application.service.message.image;


import com.example.chat.application.data.response.ChatMessageResponse;
import com.example.chat.application.exception.AccessDeniedException;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.mapper.ChatMessageMapper;
import com.example.chat.application.service.message.base.BaseChatMessageServiceImpl;
import com.example.chat.application.service.message.image.data.request.CreateImageChatMessageRequest;
import com.example.chat.application.service.message.image.data.response.CreateImageChatMessageResponse;
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
public class ImageChatMessageServiceImpl
        extends BaseChatMessageServiceImpl
        implements ImageChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageMapper messageMapper;

    public ImageChatMessageServiceImpl(
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
    public CreateImageChatMessageResponse createImageMessage(CreateImageChatMessageRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        Profile sender = profileRepository.findByOwnerInfoOwnerId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for current user with id: " + currentUserId));
        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + request.getChatId()));

        if (!chatRoom.isParticipantByUserId(currentUserId)) {
            throw new AccessDeniedException("You are not a participant of this chat");
        }

        ChatMessage message = chatRoom.addImageMessage(sender, request.getImageId());

        chatRoomRepository.save(chatRoom);
        log.info("Сообщение с типом 'Изображение' создано с id: {}", message.getId());

        ChatMessageResponse messageResponse = messageMapper.toResponse(message);
        return new CreateImageChatMessageResponse(messageResponse);
    }

}