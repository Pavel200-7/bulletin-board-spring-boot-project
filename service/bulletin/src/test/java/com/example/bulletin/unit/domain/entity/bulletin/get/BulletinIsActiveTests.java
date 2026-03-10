package com.example.bulletin.unit.domain.entity.bulletin.get;

import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class BulletinIsActiveTests {

    @Test
    public void shouldReturnTrueWhenPublished() {
        // Arrange
        Bulletin bulletin = createPublishedBulletin();

        // Act
        boolean result = bulletin.isActive();

        // Assert
        assertTrue(result);
    }

    @Test
    public void shouldReturnFalseWhenNotPublished() {
        // Arrange
        Bulletin bulletin = createBulletin();

        // Act
        boolean result = bulletin.isActive();

        // Assert
        assertFalse(result);
    }

    private Bulletin createPublishedBulletin() {
        Bulletin bulletin = createBulletin();
        bulletin.setState(BulletinState.PUBLISHED);
        return bulletin;
    }

    private Bulletin createBulletin() {
        User user = User.createUser(UUID.randomUUID(), "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Bulletin.createDraft(ownerInfo);
    }

}
