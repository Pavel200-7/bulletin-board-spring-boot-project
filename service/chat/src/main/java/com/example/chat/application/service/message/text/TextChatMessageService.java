package com.example.chat.application.service.message.text;

import com.example.chat.application.service.message.base.BaseChatMessageService;
import com.example.chat.application.service.message.text.data.request.*;
import com.example.chat.application.service.message.text.data.response.*;

public interface TextChatMessageService extends BaseChatMessageService {
    CreateTextChatMessageResponse createTextMessage(CreateTextChatMessageRequest request);
    UpdateTextChatMessageResponse updateTextMessage(UpdateTextChatMessageRequest request);
}
