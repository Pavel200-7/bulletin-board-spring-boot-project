package com.example.bulletin.unit.domain.entity.bulletin;

import com.example.bulletin.application.mapper.BulletinMapper;
import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.domain.enums.BulletinStatus;
import com.example.bulletin.domain.vo.BulletinData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
public class BulletinCreateTests {

    @Autowired
    private BulletinMapper mapper;

    @Test
    public void shouldCreateBulletinDraft()
            throws AccessDeniedException {
        // Arrange
        User user = User.createUser(UUID.randomUUID(), "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        BulletinData expected = BulletinData.builder()
                .id(UUID.randomUUID())
                .ownerId(ownerInfo.getOwnerId())
                .title(null)
                .description(null)
                .price(0)
                .rating(0)
                .status(BulletinStatus.DRAFT)
                .category(null)
                .characteristics(List.of())
                .images(List.of())
                .build();

        // Act
        Bulletin bulletin = Bulletin.createDraft(ownerInfo);
        BulletinData actual = mapper.toData(bulletin);

        // Assert
        assertTrue(expected.equalsData(actual));
    }

    @Test
    public void shouldThrowWhenUserBlocked() {
        // Arrange
        User user = User.createUser(UUID.randomUUID(), "test@example.com");
        user.setBlocked(true);
        OwnerInfo ownerInfo = new OwnerInfo(user);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () ->
                Bulletin.createDraft(ownerInfo));
    }

}
