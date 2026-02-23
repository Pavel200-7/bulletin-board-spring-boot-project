package com.example.bulletin.unit.domain.entity.bulletin.update;

import com.example.bulletin.domain.entity.*;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import org.junit.jupiter.api.Test;

import java.nio.file.AccessDeniedException;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class BulletinSetCharacteristicValueTests {

    @Test
    public void shouldThrowWhenCharacteristicOfInputValueIsNotFound()
            throws AccessDeniedException {
        // Arrange
        Bulletin bulletin = createBulletin();

        Category category = createCategory();
        Characteristic characteristic =  category.addCharacteristic("size");
        Characteristic notSetCharacteristic =  category.addCharacteristic("color");

        bulletin.setCategory(category);
        bulletin.addCharacteristic(characteristic);

        CharacteristicValue valueOfNotSetCharacteristic = notSetCharacteristic.addPossibleValue("black");

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                bulletin.setCharacteristicValue(valueOfNotSetCharacteristic));
    }

    @Test
    public void shouldSetCharacteristicValue()
            throws AccessDeniedException {
        // Arrange
        Bulletin bulletin = createBulletin();

        Category category = createCategory();
        Characteristic characteristic =  category.addCharacteristic("size");

        bulletin.setCategory(category);
        bulletin.addCharacteristic(characteristic);

        CharacteristicValue value = characteristic.addPossibleValue("small");

        // Act
        bulletin.setCharacteristicValue(value);

        // Assert
        BulletinCharacteristic bulletinCharacteristic =  bulletin.getCharacteristics().get(0);
        assertNotNull(bulletinCharacteristic.getValue());
        assertEquals(value, bulletinCharacteristic.getValue());
    }

    private Bulletin createBulletin()
            throws AccessDeniedException {
        User user = User.createUser(UUID.randomUUID(), "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        Bulletin bulletin = Bulletin.createDraft(ownerInfo);
        return bulletin;
    }

    private Category createCategory() {
        Category root = Category.createRoot("root");
        return root.createLeafyChild("child 1");
    }

}
