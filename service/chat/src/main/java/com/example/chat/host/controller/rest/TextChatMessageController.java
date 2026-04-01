package com.example.chat.host.controller.rest;

import com.example.chat.application.service.message.text.TextChatMessageService;
import com.example.chat.application.service.message.text.data.request.CreateTextChatMessageRequest;
import com.example.chat.application.service.message.text.data.request.UpdateTextChatMessageRequest;
import com.example.chat.application.service.message.text.data.response.CreateTextChatMessageResponse;
import com.example.chat.application.service.message.text.data.response.UpdateTextChatMessageResponse;
import com.example.chat.application.service.message.base.data.request.DeleteChatMessageRequest;
import com.example.chat.application.service.message.base.data.response.DeleteChatMessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chat/{chatId}/messages/text")
@RequiredArgsConstructor
public class TextChatMessageController {

    private final TextChatMessageService textChatMessageService;

    @PostMapping
    public ResponseEntity<CreateTextChatMessageResponse> createTextMessage(
            @PathVariable UUID chatId,
            @Valid @RequestBody CreateTextChatMessageRequest request) {
        request.setChatId(chatId);
        CreateTextChatMessageResponse response = textChatMessageService.createTextMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<UpdateTextChatMessageResponse> updateTextMessage(
            @PathVariable UUID chatId,
            @PathVariable UUID messageId,
            @Valid @RequestBody UpdateTextChatMessageRequest request) {
        request.setMessageId(messageId);
        UpdateTextChatMessageResponse response = textChatMessageService.updateTextMessage(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<DeleteChatMessageResponse> deleteMessage(
            @PathVariable UUID chatId,
            @PathVariable UUID messageId) {
        DeleteChatMessageResponse response = textChatMessageService.deleteMessage(new DeleteChatMessageRequest(messageId));
        return ResponseEntity.ok(response);
    }

}