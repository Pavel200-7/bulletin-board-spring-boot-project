package com.example.chat.domain.entity;

import com.example.chat.domain.entity.base.BaseEntity;
import com.example.chat.domain.enums.ChatRoomType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "chat_room")
public class ChatRoom extends BaseEntity {

    @Id
    @Column(name = "id")
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type")
    @Setter(AccessLevel.NONE)
    private ChatRoomType type;

    @Column(name = "name")
    private String name;

}
