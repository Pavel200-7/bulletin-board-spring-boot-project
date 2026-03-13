package com.example.chat.unit.domain.entity.contacts;

import com.example.chat.domain.entity.Contact;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import com.example.chat.domain.entity.base.user.User;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
public class ContactCreateTests {

    @Test
    public void shouldCreateContact() {
        // Arrange
        Profile ownerProfile = createProfile("Public Name");

        String contactName = "Friend";
        Profile contactProfile = createProfile(contactName);

        // Act
        Contact contact = Contact.createContact(ownerProfile, contactProfile);

        // Assert
        assertNotNull(contact.getId());
        assertEquals(ownerProfile, contact.getOwnerProfile());
        assertEquals(contactProfile, contact.getContactProfile());
        assertEquals(contactName, contact.getContactName());
    }

    private Profile createProfile(String publicName) {
        User user = User.createUser(UUID.randomUUID(), "owner@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Profile.createProfile(ownerInfo, publicName);
    }

}
