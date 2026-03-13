package com.example.chat.unit.domain.entity.contacts;

import com.example.chat.domain.entity.Contact;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import com.example.chat.domain.entity.base.user.User;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class ContactUpdateTests {

    @Test
    public void shouldChangeContactName() {
        // Arrange
        Profile ownerProfile = createProfile("owner");
        Profile contactProfile = createProfile("friend");
        Contact contact = Contact.createContact(ownerProfile, contactProfile);
        String newContactName = "Best Friend";

        // Act
        contact.changeContactName(newContactName);

        // Assert
        assertEquals(newContactName, contact.getContactName());
    }

    private Profile createProfile(String publicName) {
        User user = User.createUser(UUID.randomUUID(), "owner@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Profile.createProfile(ownerInfo, publicName);
    }

}
