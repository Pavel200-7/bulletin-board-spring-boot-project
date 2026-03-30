package com.example.chat.unit.domain.entity.profile;

import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.domain.entity.ChatRoom;
import com.example.chat.domain.entity.Contact;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.entity.base.user.User;
import com.example.chat.domain.enums.ChatRoomType;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;


import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class ProfileUpdateTests {

    @Test
    public void shouldChangePublicName() {
        // Arrange
        Profile profile = createProfile("Public Name");
        String newPublicName = "New Public Name";

        // Act
        profile.changePublicName(newPublicName);

        // Assert
        assertEquals(newPublicName, profile.getPublicName());
    }

    @Test
    public void shouldChangeDescription() {
        // Arrange
        Profile profile = createProfile("Public Name");
        String newDescription = "New Description";

        // Act
        profile.changeDescription(newDescription);

        // Assert
        assertEquals(newDescription, profile.getDescription());
    }

    @Test
    public void shouldChangeImage() {
        // Arrange
        Profile profile = createProfile("Public Name");
        UUID newImageId = UUID.randomUUID();

        // Act
        profile.changeImage(newImageId);

        // Assert
        assertEquals(newImageId, profile.getImageId());
    }

    @Test
    public void shouldAddContact() {
        // Arrange
        Profile ownerProfile = createProfile("Public Name");

        String contactName = "My Friend";
        Profile contactProfile = createProfile(contactName);

        // Act
        Contact contact = ownerProfile.addContact(contactProfile);

        // Assert
        assertNotNull(contact);
        assertEquals(ownerProfile, contact.getOwnerProfile());
        assertEquals(contactProfile, contact.getContactProfile());
        assertEquals(contactName, contact.getContactName());
        assertEquals(1, ownerProfile.getContacts().size());
    }

    @Test
    public void shouldTwoPartyAddChat() {
        // Arrange
        Profile ownerProfile = createProfile("Public Name");
        Profile contactProfile = createProfile("friend");
        Contact contact = ownerProfile.addContact(contactProfile);

        // Act
        ownerProfile.addChatRoom(contact);

        // Assert
        assertFalse(ownerProfile.getChatParticipants().isEmpty());
        assertFalse(contactProfile.getChatParticipants().isEmpty());

        ChatRoom chatRoom = ownerProfile.getChatParticipants().get(0).getChatRoom();
        assertEquals(ChatRoomType.TWO_PARTY, chatRoom.getType());
        assertEquals(2, chatRoom.getParticipants().size());
    }

    @Test
    public void shouldThrowWhenAddingSelfAsContact() {
        // Arrange
        Profile profile = createProfile("Public Name");

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                profile.addContact(profile));
    }

    @Test
    public void shouldThrowWhenAddingDuplicateContact() {
        // Arrange
        Profile ownerProfile = createProfile("Public Name");
        Profile contactProfile = createProfile("friend");
        ownerProfile.addContact(contactProfile);

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                ownerProfile.addContact(contactProfile));
    }

    @Test
    public void shouldRemoveContact() {
        // Arrange
        Profile ownerProfile = createProfile("Public Name");
        Profile contactProfile = createProfile("friend");
        Contact contact = ownerProfile.addContact(contactProfile);

        // Act
        ownerProfile.removeContact(contact.getId());

        // Assert
        assertTrue(ownerProfile.getContacts().isEmpty());
        assertNull(contact.getOwnerProfile());
        assertNull(contact.getContactProfile());
    }

    @Test
    public void shouldThrowWhenRemovingNonExistentContact() {
        // Arrange
        Profile ownerProfile = createProfile("Public Name");

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                ownerProfile.removeContact(UUID.randomUUID()));
    }

    @Test
    public void shouldUpdateContactName() {
        // Arrange
        Profile ownerProfile = createProfile("Public Name");
        Profile contactProfile = createProfile("friend");
        Contact contact = ownerProfile.addContact(contactProfile);
        String newContactName = "Best Friend";

        // Act
        Contact updatedContact = ownerProfile.updateContactName(contact.getId(), newContactName);

        // Assert
        assertEquals(newContactName, updatedContact.getContactName());
        assertEquals(newContactName, contact.getContactName());
    }

    @Test
    public void shouldThrowWhenUpdatingNonExistentContact() {
        // Arrange
        Profile ownerProfile = createProfile("Public Name");

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                ownerProfile.updateContactName(UUID.randomUUID(), "New Name"));
    }

    private Profile createProfile(String publicName) {
        User user = User.createUser(UUID.randomUUID(), "owner@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Profile.createProfile(ownerInfo, publicName);
    }

}