package com.example.chat.host.controller.rest;

import com.example.chat.application.service.message.image.ImageChatMessageService;
import com.example.chat.application.service.message.image.data.request.CreateImageChatMessageRequest;
import com.example.chat.application.service.message.image.data.response.CreateImageChatMessageResponse;
import com.example.chat.application.service.message.base.data.request.DeleteChatMessageRequest;
import com.example.chat.application.service.message.base.data.response.DeleteChatMessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chat/{chatId}/messages/image")
@RequiredArgsConstructor
public class ImageChatMessageController {

    private final ImageChatMessageService imageChatMessageService;

    @PostMapping
    public ResponseEntity<CreateImageChatMessageResponse> createImageMessage(
            @PathVariable UUID chatId,
            @Valid @RequestBody CreateImageChatMessageRequest request) {
        request.setChatId(chatId);
        CreateImageChatMessageResponse response = imageChatMessageService.createImageMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<DeleteChatMessageResponse> deleteMessage(
            @PathVariable UUID chatId,
            @PathVariable UUID messageId) {
        DeleteChatMessageResponse response = imageChatMessageService.deleteMessage(new DeleteChatMessageRequest(messageId));
        return ResponseEntity.ok(response);
    }
}