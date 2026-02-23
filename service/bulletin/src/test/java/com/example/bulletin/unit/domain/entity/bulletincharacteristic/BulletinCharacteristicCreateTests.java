package com.example.bulletin.unit.domain.entity.bulletincharacteristic;

import com.example.bulletin.domain.entity.*;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import org.junit.jupiter.api.Test;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class BulletinCharacteristicCreateTests {

    private Category categoryAggregate = null;

    @Test
    public void shouldCreateBulletinCharacteristic()
            throws AccessDeniedException {
        // Arrange
        Bulletin bulletin = createBulletin();
        Category category = createCategoryAggregate();
        Characteristic characteristic = category.getCharacteristics()
                .getFirst();

        // Act
        BulletinCharacteristic bulletinCharacteristic = BulletinCharacteristic.createBulletinCharacteristic(bulletin, characteristic);

        // Assert
        assertEquals(bulletin, bulletinCharacteristic.getBulletin());
        assertEquals(characteristic, bulletinCharacteristic.getName());
        assertNull(bulletinCharacteristic.getValue());
    }

    private Category createCategoryAggregate() {
        if (this.categoryAggregate == null) {
            this.categoryAggregate = Category.createRoot("some category");
            Characteristic characteristic = this.categoryAggregate.addCharacteristic("characteristic 1");
            characteristic.addPossibleValue("some characteristic value");
        }
        return this.categoryAggregate;
    }

    private Bulletin createBulletin()
            throws AccessDeniedException {
        User user = User.createUser(UUID.randomUUID(), "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Bulletin.createDraft(ownerInfo);
    }

}
