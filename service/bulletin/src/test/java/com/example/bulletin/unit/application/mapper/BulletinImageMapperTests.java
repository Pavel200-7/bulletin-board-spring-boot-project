package com.example.bulletin.unit.application.mapper;

import com.example.bulletin.application.data.response.BulletinImageResponse;
import com.example.bulletin.application.mapper.BulletinImageMapper;
import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.entity.BulletinImage;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.domain.vo.BulletinImageData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class BulletinImageMapperTests {

    @Autowired
    private BulletinImageMapper mapper;

    @Test
    public void shouldConvertCorrectlyFromEntityToData() {
        // Arrange
        Bulletin bulletin = createBulletin();
        UUID imageId = UUID.randomUUID();
        BulletinImage bulletinImage = BulletinImage.createBulletinImage(bulletin, imageId);

        BulletinImageData expected = BulletinImageData.builder()
                .id(UUID.randomUUID())
                .bulletinId(bulletin.getId())
                .imageId(imageId)
                .build();

        // Act
        BulletinImageData actual = mapper.toData(bulletinImage);

        // Assert
        assertTrue(expected.equalsData(actual));
    }

    @Test
    public void shouldConvertCorrectlyFromEntityToResponse() {
        // Arrange
        Bulletin bulletin = createBulletin();
        UUID imageId = UUID.randomUUID();
        BulletinImage bulletinImage = BulletinImage.createBulletinImage(bulletin, imageId);

        BulletinImageResponse expected = BulletinImageResponse.builder()
                .id(bulletinImage.getId())
                .bulletinId(bulletin.getId())
                .imageId(imageId)
                .build();

        // Act
        BulletinImageResponse actual = mapper.toResponse(bulletinImage);

        // Assert
        assertTrue(expected.equalsData(actual));
    }

    private Bulletin createBulletin() {
        User user = User.createUser(UUID.randomUUID(), "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Bulletin.createDraft(ownerInfo);
    }

}
