package com.example.chat.application.service.chatroom;

import com.example.chat.application.service.chatroom.data.request.*;
import com.example.chat.application.service.chatroom.data.response.*;

public interface ChatRoomService {
    GetChatResponse getChat(GetChatRequest request);
    GetUnreadMessageCountResponse getUnreadMessageCount(GetUnreadMessageCountRequest request);
    GetMessagePaginationResponse getMessagePagination(GetMessagePaginationRequest request);
    GetMessagePaginationResponse getFirstMessagePage(GetFirstMessagePageRequest request);
    GetMessagePaginationResponse getMessagesAroundLastRead(GetMessagesAroundLastReadRequest request);
    SetLastReadMessageResponse setLastReadMessage(SetLastReadMessageRequest request);
}
