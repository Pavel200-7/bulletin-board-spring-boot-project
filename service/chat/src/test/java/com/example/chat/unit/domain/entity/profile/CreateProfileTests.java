package com.example.chat.unit.domain.entity.profile;

import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.entity.base.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
public class CreateProfileTests {

    @Test
    public void shouldCreateTradeAccount() {
        // Arrange
        OwnerInfo ownerInfo = createOwnerInfo();
        String publicName = "someName";

        // Act
        Profile profile = Profile.createProfile(ownerInfo, publicName);

        // Assert
        assertEquals(ownerInfo.getOwner(),
                profile.getOwner());
        assertEquals(publicName,
                profile.getPublicName());

    }

    private OwnerInfo createOwnerInfo() {
        User user = User.createUser(UUID.randomUUID(), "owner@example.com");
        return new OwnerInfo(user);
    }

}
