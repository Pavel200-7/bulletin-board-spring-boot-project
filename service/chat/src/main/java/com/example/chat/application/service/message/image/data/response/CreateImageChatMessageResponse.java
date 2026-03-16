package com.example.chat.application.service.message.image.data.response;

import com.example.chat.application.data.response.ChatMessageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateImageChatMessageResponse {
    private ChatMessageResponse chatMessageResponse;
}
