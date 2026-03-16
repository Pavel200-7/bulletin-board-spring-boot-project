package com.example.chat.application.service.message.text.data.response;

import com.example.chat.application.data.response.ChatMessageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTextChatMessageResponse {
    private ChatMessageResponse chatMessageResponse;
}
