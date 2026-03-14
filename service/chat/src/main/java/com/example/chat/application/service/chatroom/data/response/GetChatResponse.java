package com.example.chat.application.service.chatroom.data.response;

import com.example.chat.application.data.response.ChatRoomResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetChatResponse {
    private ChatRoomResponse chatRoomResponse;
}
