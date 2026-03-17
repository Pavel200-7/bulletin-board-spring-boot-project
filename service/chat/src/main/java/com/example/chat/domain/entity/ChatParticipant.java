package com.example.chat.domain.entity;

import com.example.chat.domain.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "chat_participant")
public class ChatParticipant extends BaseEntity {

    @Id
    @Column(name = "id")
    @Setter(AccessLevel.NONE)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_profile_id")
    @Setter(AccessLevel.NONE)
    private Profile profile;

    @Column(name = "owner")
    @Setter(AccessLevel.NONE)
    private boolean owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    @Setter(AccessLevel.NONE)
    private ChatRoom chatRoom;

    @Column(name = "last_read_message_id")
    @Setter(AccessLevel.NONE)
    private UUID lastReadMessageId;

    @Column(name = "hidden")
    @Setter(AccessLevel.NONE)
    private boolean hidden;

    protected ChatParticipant() {}

    private ChatParticipant(Profile profile, ChatRoom chatRoom, boolean isOwner) {
        this.id = UUID.randomUUID();
        this.profile = profile;
        this.chatRoom = chatRoom;
        this.owner = isOwner;
        this.hidden = false;
    }

    static ChatParticipant createParticipant(Profile profile, ChatRoom chatRoom, boolean isOwner) {
        ChatParticipant participant = new ChatParticipant(profile, chatRoom, isOwner);
        profile.addChatParticipant(participant);
        return participant;
    }

    public ChatParticipant markMessageAsRead(UUID messageId) {
        this.lastReadMessageId = messageId;
        return this;
    }

    public ChatParticipant hide() {
        this.hidden = true;
        return this;
    }

    public ChatParticipant unhide() {
        this.hidden = false;
        return this;
    }

}
