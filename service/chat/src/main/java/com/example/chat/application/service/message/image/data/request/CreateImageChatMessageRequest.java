package com.example.chat.application.service.message.image.data.request;

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
public class CreateImageChatMessageRequest {
    @NotNull
    private UUID chatId;
    @NotNull
    private UUID imageId;
}
