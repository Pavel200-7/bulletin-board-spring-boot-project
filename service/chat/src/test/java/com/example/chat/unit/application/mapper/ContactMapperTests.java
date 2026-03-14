package com.example.chat.unit.application.mapper;

import com.example.chat.application.data.response.ContactResponse;
import com.example.chat.application.mapper.ContactMapper;
import com.example.chat.domain.entity.ChatParticipant;
import com.example.chat.domain.entity.ChatRoom;
import com.example.chat.domain.entity.Contact;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.entity.base.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class ContactMapperTests {

    private ContactMapper mapper = Mappers.getMapper(ContactMapper.class);

    private Profile ownerProfile;
    private Profile contactProfile;
    private Contact contact;

    @BeforeEach
    void setUp() {
        User ownerUser = User.createUser(UUID.randomUUID(), "owner@example.com");
        User contactUser = User.createUser(UUID.randomUUID(), "contact@example.com");

        OwnerInfo ownerInfo = new OwnerInfo(ownerUser);
        OwnerInfo contactInfo = new OwnerInfo(contactUser);

        ownerProfile = Profile.createProfile(ownerInfo, "Owner Name");
        contactProfile = Profile.createProfile(contactInfo, "Contact Name");

        contact = ownerProfile.addContact(contactProfile);
    }

    @Test
    void shouldFindChatId() {
        // Arrange
        ChatRoom chatRoom = ownerProfile.getChatParticipants().stream()
                .map(ChatParticipant::getChatRoom)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Chat should exist"));

        UUID chatRoomId = chatRoom.getId();

        // Act
        ContactResponse response = mapper.toResponse(contact);

        // Assert
        assertNotNull(response);
        assertEquals(chatRoomId, response.getChatId());
    }

    @Test
    void shouldMapCorrectlyWhenContactNameDiffersFromProfileName() {
        // Arrange
        String customContactName = "My Best Friend";
        contact.changeContactName(customContactName);

        // Act
        ContactResponse response = mapper.toResponse(contact);

        // Assert
        assertEquals(customContactName, response.getContactName());
        assertNotEquals(contactProfile.getPublicName(), response.getContactName());
    }

    @Test
    void shouldMapAllFieldsCorrectly() {
        // Arrange
        String customContactName = "Best Friend Forever";
        contact.changeContactName(customContactName);

        ChatRoom chatRoom = ownerProfile.getChatParticipants().stream()
                .map(ChatParticipant::getChatRoom)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Chat should exist"));

        UUID chatRoomId = chatRoom.getId();

        // Act
        ContactResponse response = mapper.toResponse(contact);

        // Assert
        assertEquals(contact.getId(), response.getId());
        assertEquals(ownerProfile.getId(), response.getOwnerProfileId());
        assertEquals(contactProfile.getId(), response.getContactProfileId());
        assertEquals(customContactName, response.getContactName());
        assertEquals(chatRoomId, response.getChatId());
    }

}