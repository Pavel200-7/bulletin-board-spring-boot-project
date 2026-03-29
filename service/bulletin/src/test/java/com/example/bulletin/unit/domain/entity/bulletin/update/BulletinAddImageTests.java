package com.example.bulletin.unit.domain.entity.bulletin.update;

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
public class BulletinAddImageTests {

    @Test
    public void shouldAddImage() {
        // Arrange
        UUID imageId = UUID.randomUUID();
        Bulletin bulletin = createBulletin();

        // Act
        bulletin.addImage(imageId);

        // Assert
        assertFalse(bulletin.getImages().isEmpty());
    }

    @Test
    void shouldSetFirstImageAsMain() {
        // Arrange
        Bulletin bulletin = createBulletin();
        bulletin.addImage(UUID.randomUUID());
        UUID imageId = UUID.randomUUID();

        // Act
        bulletin.addImage(imageId);

        // Assert
        BulletinImage addedImage = getImageById(bulletin, imageId);
        assertFalse(addedImage.isMain());
    }

    @Test
    void shouldNotSetSecondImageAsMain() {
        // Arrange
        UUID imageId = UUID.randomUUID();
        Bulletin bulletin = createBulletin();

        // Act
        bulletin.addImage(imageId);

        // Assert
        BulletinImage addedImage = getImageById(bulletin, imageId);
        assertTrue(addedImage.isMain());
    }

    private BulletinImage getImageById(Bulletin bulletin, UUID imageId) {
        return bulletin.getImages().stream()
                .filter(image -> image.getImageId() == imageId)
                .findFirst()
                .get();

    }

    private Bulletin createBulletin() {
        User user = User.createUser(UUID.randomUUID(), "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Bulletin.createDraft(ownerInfo);
    }

}
