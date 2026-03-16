package com.example.chat.application.service.message.base;

import com.example.chat.application.service.message.base.data.request.DeleteChatMessageRequest;
import com.example.chat.application.service.message.base.data.response.DeleteChatMessageResponse;

public interface BaseChatMessageService {
    DeleteChatMessageResponse deleteMessage(DeleteChatMessageRequest request);
}
