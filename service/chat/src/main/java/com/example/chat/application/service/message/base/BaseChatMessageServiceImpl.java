package com.example.chat.application.service.message.base;

import com.example.chat.application.exception.AccessDeniedException;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.service.message.base.data.request.DeleteChatMessageRequest;
import com.example.chat.application.service.message.base.data.response.DeleteChatMessageResponse;
import com.example.chat.domain.entity.ChatMessage;
import com.example.chat.domain.entity.Profile;
import com.example.chat.infrastructure.repository.ChatMessageRepository;
import com.example.chat.infrastructure.repository.ProfileRepository;
import com.example.chat.infrastructure.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseChatMessageServiceImpl implements BaseChatMessageService {

    protected final ChatMessageRepository messageRepository;
    protected final ProfileRepository profileRepository;
    protected final SecurityService securityService;

    @Override
    public DeleteChatMessageResponse deleteMessage(DeleteChatMessageRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        ChatMessage message = messageRepository.findById(request.getMessageId())
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
        Profile profile = profileRepository.findByOwnerInfoOwnerId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        if (!message.isOwner(profile)) {
            throw new AccessDeniedException("You are not a owner of this message");
        }

        messageRepository.delete(message);
        return new DeleteChatMessageResponse();
    }

}
