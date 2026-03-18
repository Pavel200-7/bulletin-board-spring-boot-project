package com.example.chat.application.service.chatroom.data.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class GetFirstMessagePageRequest {

    @NotNull
    private UUID chatId;

    @NotNull
    @Min(0)
    @Max(100)
    private int size;
}