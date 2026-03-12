package com.example.chat.domain.entity;

import com.example.chat.domain.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import tools.jackson.core.ObjectReadContext;

import java.time.LocalDateTime;
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
    private UUID lastReadMessageId;

    @Column(name = "hidden")
    private boolean hidden;

}
