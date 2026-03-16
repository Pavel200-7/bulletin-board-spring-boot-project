package com.example.chat.application.service.message.image;

import com.example.chat.application.service.message.base.BaseChatMessageService;
import com.example.chat.application.service.message.image.data.request.*;
import com.example.chat.application.service.message.image.data.response.*;

public interface ImageChatMessageService extends BaseChatMessageService {
    CreateImageChatMessageResponse createImageMessage(CreateImageChatMessageRequest request);
}
