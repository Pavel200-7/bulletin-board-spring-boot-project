package com.example.bulletin.unit.domain.entity.bulletinimage;

import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.entity.BulletinImage;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class BulletinImageUpdateTests {

    @Test
    public void shouldSetMain()
            throws AccessDeniedException {
        // Arrange
        BulletinImage bulletinImage = createBulletinImage();

        // Act
        bulletinImage.setMain();

        // Assert
        assertTrue(bulletinImage.isMain());
    }

    @Test
    public void shouldUnsetMain()
            throws AccessDeniedException {
        // Arrange
        BulletinImage bulletinImage = createBulletinImage();
        bulletinImage.setMain();

        // Act
        bulletinImage.unsetMain();

        // Assert
        assertFalse(bulletinImage.isMain());
    }

    private BulletinImage createBulletinImage()
            throws AccessDeniedException {
        Bulletin bulletin = createBulletin();
        UUID imageId = UUID.randomUUID();
        return BulletinImage.createBulletinImage(bulletin, imageId);
    }


    private Bulletin createBulletin()
            throws AccessDeniedException {
        User user = User.createUser(UUID.randomUUID(), "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Bulletin.createDraft(ownerInfo);
    }

}
