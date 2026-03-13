package com.example.chat.domain.entity;

import com.example.chat.domain.entity.base.BaseEntity;
import com.example.chat.domain.enums.ChatRoomType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;
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

    @OneToMany(mappedBy = "chatRoom",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ChatParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ChatMessage> messages = new ArrayList<>();

    protected ChatRoom() {}

    private ChatRoom(ChatRoomType type, String name) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.name = name;
    }

    static ChatRoom createTwoPartyChat(Profile creator, Profile otherParticipant) {
        validateTwoPartyChatCreation(creator, otherParticipant);

        ChatRoom chatRoom = new ChatRoom(ChatRoomType.TWO_PARTY, null);

        chatRoom.addParticipant(creator, true);
        chatRoom.addParticipant(otherParticipant, false);

        return chatRoom;
    }

    private static void validateTwoPartyChatCreation(Profile creator, Profile otherParticipant) {
        if (creator.getId().equals(otherParticipant.getId())) {
            throw new IllegalStateException("Cannot create chat with self");
        }

        if (creator.hasExistingTwoPartyChat(otherParticipant)) {
            throw new IllegalStateException("Two-party chat already exists between these profiles");
        }
    }

    ChatParticipant addParticipant(Profile profile, boolean isOwner) {
        validateParticipantAddition(profile);
        ChatParticipant participant = ChatParticipant.createParticipant(profile, this, isOwner);

        this.participants.add(participant);
        profile.addChatParticipant(participant);

        return participant;
    }

    private void validateParticipantAddition(Profile profile) {
        if (hasParticipant(profile)) {
            throw new IllegalStateException("Profile is already a participant in this chat");
        }

        if (this.type == ChatRoomType.TWO_PARTY && this.participants.size() >= 2) {
            throw new IllegalStateException("Two-party chat cannot have more than 2 participants");
        }
    }

    private boolean hasParticipant(Profile profile) {
        return participants.stream()
                .anyMatch(p -> p.getProfile().getId()
                        .equals(profile.getId()));
    }

    public ChatRoom rename(String newName) {
        if (this.type == ChatRoomType.TWO_PARTY) {
            throw new IllegalStateException("Cannot rename two-party chat");
        }
        this.name = newName;
        return this;
    }

    void removeParticipant(ChatParticipant participant) {
        if (this.type == ChatRoomType.TWO_PARTY) {
            throw new IllegalStateException("Cannot remove participant from two-party chat");
        }

        if (!participant.getChatRoom().getId().equals(this.getId())) {
            throw new IllegalStateException("This participant does not belong to this chat");
        }

        this.participants.remove(participant);
        participant.getProfile().removeChatParticipant(participant.getId());
    }

    public ChatMessage addTextMessage(Profile sender, String text) {
        validateMessageSender(sender);
        ChatMessage message = ChatMessage.createTextMessage(sender, this, text);
        this.messages.add(message);

        return message;
    }

    public ChatMessage addImageMessage(Profile sender, UUID imageId) {
        validateMessageSender(sender);
        ChatMessage message = ChatMessage.createImageMessage(sender, this, imageId);
        this.messages.add(message);

        return message;
    }

    private void validateMessageSender(Profile sender) {
        if (!hasParticipant(sender)) {
            throw new IllegalStateException("Only participants can send messages to this chat");
        }
    }

}
