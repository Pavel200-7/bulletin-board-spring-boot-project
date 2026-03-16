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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_room_id")
    @Setter(AccessLevel.NONE)
    private ChatRoom chatRoom;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type")
    @Setter(AccessLevel.NONE)
    private ChatMessageType type;

    @Column(name = "updated")
    @Setter(AccessLevel.NONE)
    private boolean updated;

    @Column(name = "read")
    @Setter(AccessLevel.NONE)
    private boolean read;

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
        validateTextContent(text);
        return new ChatMessage(sender, chatRoom, ChatMessageType.TEXT, text);
    }

    static ChatMessage createImageMessage(Profile sender, ChatRoom chatRoom, UUID imageId) {
        validateImageId(imageId);
        return new ChatMessage(sender, chatRoom, ChatMessageType.IMAGE, imageId.toString());
    }

    private static void validateImageId(UUID imageId) {
        if (imageId == null) {
            throw new IllegalStateException("Image ID cannot be null");
        }
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

    public ChatMessage update(String newContent) {
        validateUpdate(newContent);
        validateTextContent(newContent);
        this.content = newContent;
        this.updated = true;
        return this;
    }

    private void validateUpdate(String newContent) {
        if (!this.isText()) {
            throw new IllegalStateException("You can update only text message");
        }

        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalStateException("Message content cannot be empty");
        }
    }

    private static void validateTextContent(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalStateException("Message text cannot be empty");
        }
    }

    public boolean isOwner(Profile profile) {
        return this.sender.getId().equals(profile.getId());
    }

    public void setRead(Profile readingProfile) {
        if (this.isOwner(readingProfile)) {
            throw new IllegalStateException("Owner can not set message read");
        }
        this.read = true;
    }

    public boolean isOlderThan(ChatMessage other) {
        return this.getCreatedAt()
                .isBefore(other.getCreatedAt());
    }

    public boolean isYoungerThan(ChatMessage other) {
        return !isOlderThan(other);
    }

}
