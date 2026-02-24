package com.example.bulletin.unit.domain.entity.bulletin.update;

import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.entity.BulletinImage;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import org.junit.jupiter.api.Test;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class BulletinRemoveImageTests {

    @Test
    public void shouldRemoveImage()
            throws AccessDeniedException {
        // Arrange
        UUID imageId = UUID.randomUUID();
        Bulletin bulletin = createBulletin();
        BulletinImage image = bulletin.addImage(imageId);

        // Act
        bulletin.removeImage(image.getId());

        // Assert
        assertTrue(bulletin.getImages().isEmpty());
    }

    @Test
    public void shouldThrowWhenImageIsNotFound()
            throws AccessDeniedException {
        // Arrange
        UUID imageId = UUID.randomUUID();
        Bulletin bulletin = createBulletin();
        BulletinImage image = bulletin.addImage(imageId);
        bulletin.removeImage(image.getId());

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                bulletin.removeImage(image.getId()));
    }

    private Bulletin createBulletin()
            throws AccessDeniedException {
        User user = User.createUser(UUID.randomUUID(), "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Bulletin.createDraft(ownerInfo);
    }

}
