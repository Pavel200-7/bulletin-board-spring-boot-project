package com.example.chat.domain.entity;

import com.example.chat.domain.entity.base.BaseEntity;
import com.example.chat.domain.enums.ChatMessageType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "chat_participant")
public class ChatMessage extends BaseEntity {

    @Id
    @Column(name = "id")
    @Setter(AccessLevel.NONE)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    @Setter(AccessLevel.NONE)
    private Profile sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    @Setter(AccessLevel.NONE)
    private ChatRoom chatRoom;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type")
    @Setter(AccessLevel.NONE)
    private ChatMessageType type;

    @Column(name = "content")
    private String content;

    protected ChatMessage() {}

    private ChatMessage(Profile sender, ChatRoom chatRoom, ChatMessageType type, String content) {
        this.id = UUID.randomUUID();
        this.sender = sender;
        this.chatRoom = chatRoom;
        this.type = type;
        this.content = content;
    }

    static ChatMessage createTextMessage(Profile sender, ChatRoom chatRoom, String text) {
        return new ChatMessage(sender, chatRoom, ChatMessageType.TEXT, text);
    }

    static ChatMessage createImageMessage(Profile sender, ChatRoom chatRoom, UUID imageId) {
        return new ChatMessage(sender, chatRoom, ChatMessageType.IMAGE, imageId.toString());
    }

    public boolean isText() {
        return type == ChatMessageType.TEXT;
    }

    public boolean isImage() {
        return type == ChatMessageType.IMAGE;
    }

    public UUID getImageId() {
        if (!isImage()) {
            throw new IllegalStateException("This message is not an image");
        }
        return UUID.fromString(content);
    }

}
