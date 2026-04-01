package com.example.chat.host.controller.rest;

import com.example.chat.application.service.chatroom.ChatRoomService;
import com.example.chat.application.service.chatroom.data.request.*;
import com.example.chat.application.service.chatroom.data.response.GetChatResponse;
import com.example.chat.application.service.chatroom.data.response.GetMessagePaginationResponse;
import com.example.chat.application.service.chatroom.data.response.GetUnreadMessageCountResponse;
import com.example.chat.application.service.chatroom.data.response.SetLastReadMessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/{chatId}")
    public ResponseEntity<GetChatResponse> getChat(@PathVariable UUID chatId) {
        GetChatResponse response = chatRoomService.getChat(new GetChatRequest(chatId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{chatId}/unread-count")
    public ResponseEntity<GetUnreadMessageCountResponse> getUnreadMessageCount(@PathVariable UUID chatId) {
        GetUnreadMessageCountResponse response = chatRoomService.getUnreadMessageCount(new GetUnreadMessageCountRequest(chatId));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{chatId}/messages/search")
    public ResponseEntity<GetMessagePaginationResponse> getMessagePagination(
            @PathVariable UUID chatId,
            @Valid @RequestBody GetMessagePaginationRequest request) {
        request.setChatId(chatId);
        GetMessagePaginationResponse response = chatRoomService.getMessagePagination(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{chatId}/messages/first")
    public ResponseEntity<GetMessagePaginationResponse> getFirstMessagePage(
            @PathVariable UUID chatId,
            @Valid @RequestBody GetFirstMessagePageRequest request) {
        request.setChatId(chatId);
        GetMessagePaginationResponse response = chatRoomService.getFirstMessagePage(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{chatId}/messages/last-read")
    public ResponseEntity<GetMessagePaginationResponse> getMessagesAroundLastRead(
            @PathVariable UUID chatId,
            @Valid @RequestBody GetMessagesAroundLastReadRequest request) {
        request.setChatId(chatId);
        GetMessagePaginationResponse response = chatRoomService.getMessagesAroundLastRead(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{chatId}/messages/{messageId}/last-read")
    public ResponseEntity<SetLastReadMessageResponse> setLastReadMessage(
            @PathVariable UUID chatId,
            @PathVariable UUID messageId) {
        SetLastReadMessageResponse response = chatRoomService.setLastReadMessage(new SetLastReadMessageRequest(messageId));
        return ResponseEntity.ok(response);
    }

}