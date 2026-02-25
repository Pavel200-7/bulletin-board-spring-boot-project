package com.example.bulletin.unit.domain.entity.bulletin.update;

import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.entity.BulletinImage;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import org.junit.jupiter.api.Test;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class BulletinSetMainImageTests {

    @Test
    public void shouldSetMain() {
        // Arrange
        Bulletin bulletin = createBulletin();
        BulletinImage image = bulletin.addImage(UUID.randomUUID());

        // Act
        bulletin.setMainImage(image.getId());

        // Assert
        assertTrue(image.isMain());
    }

    @Test
    public void assertThrowsWhenImageNotFound() {
        // Arrange
        Bulletin bulletin = createBulletin();
        BulletinImage image = bulletin.addImage(UUID.randomUUID());
        bulletin.removeImage(image.getId());

        // Act
        assertThrows(IllegalStateException.class, () ->
                bulletin.setMainImage(image.getId()));
    }

    @Test
    public void shouldUnsetMainInOtherImages() {
        // Arrange
        Bulletin bulletin = createBulletin();
        BulletinImage image1 = bulletin.addImage(UUID.randomUUID());
        BulletinImage image2 = bulletin.addImage(UUID.randomUUID());

        bulletin.setMainImage(image1.getId());

        // Act
        bulletin.setMainImage(image2.getId());

        // Assert
        assertFalse(image1.isMain());
    }

    private Bulletin createBulletin() {
        User user = User.createUser(UUID.randomUUID(), "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Bulletin.createDraft(ownerInfo);
    }
}
