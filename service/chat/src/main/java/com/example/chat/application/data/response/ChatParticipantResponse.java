package com.example.chat.application.data.response;

import com.example.chat.domain.entity.ChatRoom;
import com.example.chat.domain.entity.Profile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Setter;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class ChatParticipantResponse {
    private UUID id;
    private UUID profileId;
    private boolean owner;
    private UUID chatRoomId;
    private UUID lastReadMessageId;
}
