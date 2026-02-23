package com.example.bulletin.unit.domain.entity.bulletinimage;

import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.entity.BulletinImage;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import org.junit.jupiter.api.Test;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BulletinImageCreateTests {

    @Test
    public void shouldCreateBulletinImage()
            throws AccessDeniedException {
        // Arrange
        Bulletin bulletin = createBulletin();
        UUID imageId = UUID.randomUUID();

        // Act
        BulletinImage bulletinImage = BulletinImage.createBulletinImage(bulletin, imageId);

        // Assert
        assertEquals(bulletin, bulletinImage.getBulletin());
        assertEquals(imageId, bulletinImage.getImageId());
    }

    private Bulletin createBulletin()
            throws AccessDeniedException {
        User user = User.createUser(UUID.randomUUID(), "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Bulletin.createDraft(ownerInfo);
    }

}
