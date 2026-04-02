package com.example.chat.host.controller.websocket;

import com.example.chat.application.service.message.base.data.request.DeleteChatMessageRequest;
import com.example.chat.application.service.message.text.TextChatMessageService;
import com.example.chat.application.service.message.text.data.request.CreateTextChatMessageRequest;
import com.example.chat.application.service.message.text.data.request.UpdateTextChatMessageRequest;
import com.example.chat.application.service.message.text.data.response.CreateTextChatMessageResponse;
import com.example.chat.host.controller.websocket.data.response.ChatMessageWebSocketDto;
import com.example.chat.host.controller.websocket.data.response.DeleteMessageWebSocketDto;
import com.example.chat.infrastructure.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatMessageWebSocketController {

    private final TextChatMessageService textChatMessageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final SecurityService securityService;


     //     /app/chat/{chatId}/message/create
    @MessageMapping("/chat/{chatId}/message/create")
    public void createMessage(
            @DestinationVariable UUID chatId,
            CreateTextChatMessageRequest request,
            Principal principal) {
//        log.info("WebSocket create message for chat: {}, from: {}", chatId, securityService.getCurrentUsername());

        log.info("WebSocket create message for chat: {}, from: {}", chatId, principal.getName());
        request.setChatId(chatId);
        CreateTextChatMessageResponse response = textChatMessageService.createTextMessage(request);

        messagingTemplate.convertAndSendToUser(
                securityService.getCurrentUsername(),
                "/queue/reply",
                response
        );

        messagingTemplate.convertAndSend(
                "/topic/chat/" + chatId,
                ChatMessageWebSocketDto.fromResponse(response.getChatMessageResponse())
        );
    }

    //     /app/chat/{chatId}/message/{messageId}/update
    @MessageMapping("/chat/{chatId}/message/{messageId}/update")
    public void updateMessage(
            @DestinationVariable UUID chatId,
            @DestinationVariable UUID messageId,
            UpdateTextChatMessageRequest request,
            Principal principal) {
        log.info("WebSocket update message: {} in chat: {}, from: {}", messageId, chatId, principal.getName());
        request.setMessageId(messageId);
        var response = textChatMessageService.updateTextMessage(request);

        messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/reply",
                response
        );

        messagingTemplate.convertAndSend(
                "/topic/chat/" + chatId + "/updates",
                ChatMessageWebSocketDto.fromResponse(response.getChatMessageResponse())

        );
    }

    /**
     * Удаление сообщения через WebSocket
     * Клиент отправляет: /app/chat/{chatId}/message/{messageId}/delete
     */
    @MessageMapping("/chat/{chatId}/message/{messageId}/delete")
    public void deleteMessage(
            @DestinationVariable UUID chatId,
            @DestinationVariable UUID messageId,
            Principal principal) {
        log.info("WebSocket delete message: {} in chat: {}, from: {}", messageId, chatId, principal.getName());
        var response = textChatMessageService.deleteMessage(new DeleteChatMessageRequest(messageId));
        messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/reply",
                response
        );

        DeleteMessageWebSocketDto payload = DeleteMessageWebSocketDto.builder()
                .messageId(messageId)
                .build();

        messagingTemplate.convertAndSend(
                "/topic/chat/" + chatId + "/deletes",
                new DeleteMessageWebSocketDto(messageId)
        );
    }

}