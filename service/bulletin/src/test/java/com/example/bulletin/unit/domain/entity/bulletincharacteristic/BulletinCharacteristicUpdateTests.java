package com.example.bulletin.unit.domain.entity.bulletincharacteristic;

import com.example.bulletin.domain.entity.*;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import org.junit.jupiter.api.Test;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class BulletinCharacteristicUpdateTests {

    private Category categoryAggregate = null;

    @Test
    public void shouldSetBulletinCharacteristicValue()
            throws AccessDeniedException {
        // Arrange
        Bulletin bulletin = createBulletin();
        Category category = createCategoryAggregate();
        Characteristic characteristic = category.getCharacteristics()
                .getFirst();
        CharacteristicValue characteristicValue = characteristic.getPossibleValues()
                .getFirst();

        BulletinCharacteristic bulletinCharacteristic = BulletinCharacteristic.createBulletinCharacteristic(bulletin, characteristic);

        // Act
        bulletinCharacteristic.setValue(characteristicValue);

        // Assert
        assertEquals(characteristicValue,
                bulletinCharacteristic.getValue());
    }

    @Test
    public void shouldThrowWhenValueIsOfCharacteristicNotExistingInBulletin()
            throws AccessDeniedException {
        // Arrange
        Bulletin bulletin = createBulletin();
        Category category = createCategoryAggregate();
        Characteristic characteristic = category.getCharacteristics()
                .getFirst();
        BulletinCharacteristic bulletinCharacteristic = BulletinCharacteristic.createBulletinCharacteristic(bulletin, characteristic);

        Characteristic anotherCharacteristic = category.addCharacteristic("another characteristic");
        CharacteristicValue anotherCharacteristicValue = anotherCharacteristic.addPossibleValue("another characteristic value");

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                bulletinCharacteristic.setValue(anotherCharacteristicValue));
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
