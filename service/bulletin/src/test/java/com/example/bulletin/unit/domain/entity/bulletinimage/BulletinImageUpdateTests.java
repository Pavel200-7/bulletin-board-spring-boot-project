package com.example.bulletin.unit.domain.entity.bulletinimage;

import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.entity.BulletinImage;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class BulletinImageUpdateTests {

    @Test
    public void shouldSetMain() {
        // Arrange
        BulletinImage bulletinImage = createBulletinImage();

        // Act
        bulletinImage.setMain();

        // Assert
        assertTrue(bulletinImage.isMain());
    }

    @Test
    public void shouldUnsetMain() {
        // Arrange
        BulletinImage bulletinImage = createBulletinImage();
        bulletinImage.setMain();

        // Act
        bulletinImage.unsetMain();

        // Assert
        assertFalse(bulletinImage.isMain());
    }

    private BulletinImage createBulletinImage() {
        Bulletin bulletin = createBulletin();
        UUID imageId = UUID.randomUUID();
        return BulletinImage.createBulletinImage(bulletin, imageId);
    }


    private Bulletin createBulletin() {
        User user = User.createUser(UUID.randomUUID(), "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Bulletin.createDraft(ownerInfo);
    }

}
