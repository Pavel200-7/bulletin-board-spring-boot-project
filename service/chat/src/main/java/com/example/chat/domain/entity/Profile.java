package com.example.chat.domain.entity;

import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.domain.entity.base.BaseEntity;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.enums.ChatRoomType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "profile")
public class Profile extends BaseEntity {

    @Id
    @Column(name = "id")
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Embedded
    @Delegate
    @Setter(AccessLevel.NONE)
    private OwnerInfo ownerInfo;

    @Column(name = "public_name")
    @Setter(AccessLevel.NONE)
    private String publicName;

    @Column(name = "description")
    @Setter(AccessLevel.NONE)
    private String description;

    @Column(name = "image_id")
    @Setter(AccessLevel.NONE)
    private UUID imageId;

    @OneToMany(mappedBy = "ownerProfile",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Contact> contacts = new ArrayList<>();

    @OneToMany(mappedBy = "profile",
            fetch = FetchType.LAZY)
    private List<ChatParticipant> chatParticipants = new ArrayList<>();

    protected Profile() {};

    private Profile(OwnerInfo ownerInfo, String publicName) {
        this.id = UUID.randomUUID();
        this.ownerInfo = ownerInfo;
        this.publicName = publicName;
    }

    public static Profile createProfile(OwnerInfo ownerInfo, String publicName) { return new Profile(ownerInfo, publicName); }

    public Profile changePublicName(String newPublicName) {
        this.publicName = newPublicName;
        return this;
    }

    public Profile changeDescription(String newDescription) {
        this.description = newDescription;
        return this;
    }

    public Profile changeImage(UUID newImageId) {
        this.imageId = newImageId;
        return this;
    }

    public Contact addContact(Profile contactProfile) {
        validateContactAddition(contactProfile);
        Contact contact = Contact.createContact(this, contactProfile);
        this.contacts.add(contact);

        ChatRoom.createTwoPartyChat(this, contactProfile);

        return contact;
    }

    private void validateContactAddition(Profile contactProfile) {
        if (this.getId().equals(contactProfile.getId())) {
            throw new IllegalStateException("Cannot add self as contact");
        }

        if (hasExistingContact(contactProfile)) {
            throw new IllegalStateException("Contact already exists");
        }
    }

    private boolean hasExistingContact(Profile contactProfile) {
        return contacts.stream()
                .anyMatch(contact -> contact.getContactProfile().getId().equals(contactProfile.getId()));
    }

    public Profile removeContact(UUID contactId) {
        Contact contact = findContact(contactId)
                .orElseThrow(() -> new IllegalStateException("Contact not found"));
        contact.delete();
        return this;
    }

    void removeContact(Contact contact) {
        if (contact.getOwnerProfile() != this) {
            throw new IllegalStateException("This contact does not belong to this profile");
        }
        this.contacts.remove(contact);
    }

    public Contact updateContactName(UUID contactId, String newContactName) {
        Contact contact = findContact(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
        contact.changeContactName(newContactName);
        return contact;
    }

    private Optional<Contact> findContact(UUID contactId) {
        return contacts.stream()
                .filter(contact -> contact.getId().equals(contactId))
                .findFirst();
    }

    boolean hasExistingTwoPartyChat(Profile otherParticipant) {
        return chatParticipants.stream()
                .map(ChatParticipant::getChatRoom)
                .filter(chatRoom -> chatRoom.getType() == ChatRoomType.TWO_PARTY)
                .anyMatch(chatRoom ->
                        chatRoom.getParticipants().stream()
                                .map(ChatParticipant::getProfile)
                                .map(Profile::getId)
                                .anyMatch(id -> id.equals(otherParticipant.getId()))
                );
    }

    void addChatParticipant(ChatParticipant participant) {
        if (hasExistingChatParticipant(participant)) {
            throw new IllegalStateException("Chat participant already exists");
        }
        this.chatParticipants.add(participant);
    }

    private boolean hasExistingChatParticipant(ChatParticipant participant) {
        return this.chatParticipants.stream()
                .anyMatch(cp -> cp.getId().equals(participant.getId()));
    }

    void removeChatParticipant(UUID id) {
        ChatParticipant participant = findChatParticipant(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat participant not found"));
        this.chatParticipants.remove(participant);
    }

    private Optional<ChatParticipant> findChatParticipant(UUID chatParticipantId) {
        return this.chatParticipants.stream()
                .filter(cp -> cp.getId().equals(chatParticipantId))
                .findFirst();
    }

}
