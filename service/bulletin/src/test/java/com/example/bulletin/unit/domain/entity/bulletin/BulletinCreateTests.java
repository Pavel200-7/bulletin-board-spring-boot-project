package com.example.bulletin.unit.domain.entity.bulletin;

import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.domain.enums.BulletinStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class BulletinCreateTests {

    @Test
    public void shouldCreateBulletinDraft() {
        // Arrange
        User user = User.createUser(UUID.randomUUID(), "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);

        // Act
        Bulletin bulletin = Bulletin.createDraft(ownerInfo);

        // Assert
        assertEquals(ownerInfo.getOwner(),
                bulletin.getOwner());
    }

}
