package com.example.chat.application.service.chatroom.data.response;

import com.example.chat.application.data.response.ChatMessageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetMessagePaginationResponse {
    private Page<ChatMessageResponse> chatMessagePage;
}
