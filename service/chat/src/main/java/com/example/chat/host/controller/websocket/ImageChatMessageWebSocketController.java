package com.example.chat.host.controller.websocket;

import com.example.chat.application.service.message.base.data.request.DeleteChatMessageRequest;
import com.example.chat.application.service.message.base.data.response.DeleteChatMessageResponse;
import com.example.chat.application.service.message.image.ImageChatMessageService;
import com.example.chat.application.service.message.image.data.request.CreateImageChatMessageRequest;
import com.example.chat.application.service.message.image.data.response.CreateImageChatMessageResponse;
import com.example.chat.host.controller.websocket.data.response.ChatMessageWebSocketDto;
import com.example.chat.host.controller.websocket.data.response.DeleteMessageWebSocketDto;
import com.example.chat.host.controller.websocket.data.response.base.WebSocketMessageDto;
import com.example.chat.host.controller.websocket.data.response.enums.WebSocketMessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ImageChatMessageWebSocketController {

    private final ImageChatMessageService imageChatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Создание image сообщения через WebSocket
     * Клиент отправляет: /app/chat/{chatId}/message/image/create
     */
    @MessageMapping("/chat/{chatId}/message/image/create")
    public void createImageMessage(
            @DestinationVariable UUID chatId,
            CreateImageChatMessageRequest request,
            Principal principal) {
        log.info("WebSocket create image message for chat: {}, from: {}", chatId, principal.getName());
        request.setChatId(chatId);
        CreateImageChatMessageResponse response = imageChatMessageService.createImageMessage(request);

        messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/reply",
                response
        );

        messagingTemplate.convertAndSend(
                "/topic/chat/" + chatId,
                new WebSocketMessageDto(
                        WebSocketMessageType.MESSAGE_CREATED,
                        ChatMessageWebSocketDto.fromResponse(response.getChatMessageResponse()))
        );
    }

    /**
     * Удаление image сообщения через WebSocket
     * Клиент отправляет: /app/chat/{chatId}/message/image/{messageId}/delete
     */
    @MessageMapping("/chat/{chatId}/message/image/{messageId}/delete")
    public void deleteImageMessage(
            @DestinationVariable UUID chatId,
            @DestinationVariable UUID messageId,
            Principal principal) {
        log.info("WebSocket delete image message: {} in chat: {}, from: {}", messageId, chatId, principal.getName());
        DeleteChatMessageResponse response = imageChatMessageService.deleteMessage(
                new DeleteChatMessageRequest(messageId)
        );

        messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/reply",
                response
        );

        messagingTemplate.convertAndSend(
                "/topic/chat/" + chatId,
                new WebSocketMessageDto(
                        WebSocketMessageType.MESSAGE_DELETED,
                        new DeleteMessageWebSocketDto(messageId))
        );
    }

}