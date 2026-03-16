package com.example.chat.application.service.message.text.data.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTextChatMessageRequest {
    @NotNull
    private UUID messageId;
    @NotBlank
    private String newText;
}
