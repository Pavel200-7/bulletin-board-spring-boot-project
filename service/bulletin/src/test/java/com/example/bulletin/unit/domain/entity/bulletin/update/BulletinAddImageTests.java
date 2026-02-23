package com.example.bulletin.unit.domain.entity.bulletin.update;

import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import org.junit.jupiter.api.Test;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class BulletinAddImageTests {

    @Test
    public void shouldAddImage()
            throws AccessDeniedException {
        // Arrange
        UUID imageId = UUID.randomUUID();
        Bulletin bulletin = createBulletin();

        // Act
        bulletin.addImage(imageId);

        // Assert
        assertFalse(bulletin.getImages().isEmpty());
    }

    private Bulletin createBulletin()
            throws AccessDeniedException {
        User user = User.createUser(UUID.randomUUID(), "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Bulletin.createDraft(ownerInfo);
    }

}
