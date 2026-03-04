package com.example.bulletin.unit.domain.entity.bulletin.update;

import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.entity.BulletinCharacteristic;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class BulletinRemoveCharacteristicTests {

    @Test
    public void shouldRemoveCharacteristic() {
        // Arrange
        Category category = createLeafyCategory();
        Bulletin bulletin = createBulletinWithSetCategory(category);
        Characteristic characteristic = createCharacteristic(category);
        BulletinCharacteristic added = bulletin.addCharacteristic(characteristic);

        // Act
        bulletin.removeCharacteristic(added.getId());

        // Assert
        assertTrue(bulletin.getCharacteristics().isEmpty());
    }

    @Test
    public void shouldThrowWhenCharacteristicIsNotExist() {
        // Arrange
        Category category = createLeafyCategory();
        Bulletin bulletin = createBulletinWithSetCategory(category);

        Characteristic characteristic = createCharacteristic(category);
        bulletin.addCharacteristic(characteristic);

        Bulletin anotherBulletin = createBulletinWithSetCategory(category);
        Characteristic anotherCharacteristic = createCharacteristic(category);
        BulletinCharacteristic anotherAdded = anotherBulletin.addCharacteristic(anotherCharacteristic);

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                bulletin.removeCharacteristic(anotherAdded.getId()));
    }

    @Test
    public void shouldThrowWhenCharacteristicBelongsToAnotherBulletin() {
        // Arrange
        Category category = createLeafyCategory();
        Bulletin bulletin = createBulletinWithSetCategory(category);

        Characteristic characteristic = createCharacteristic(category);
        BulletinCharacteristic notAdded = BulletinCharacteristic.createBulletinCharacteristic(bulletin, characteristic);

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                bulletin.removeCharacteristic(notAdded.getId()));

    }

    @Test
    public void shouldRemoveOnlySpecifiedCharacteristicWhenMultipleExist() {
        // Arrange
        Category category = createLeafyCategory();
        Bulletin bulletin = createBulletinWithSetCategory(category);

        Characteristic characteristic1 = createCharacteristicWithName(category, "Color");
        Characteristic characteristic2 = createCharacteristicWithName(category, "Size");
        Characteristic characteristic3 = createCharacteristicWithName(category, "Material");

        BulletinCharacteristic added1 = bulletin.addCharacteristic(characteristic1);
        BulletinCharacteristic added2 = bulletin.addCharacteristic(characteristic2);
        BulletinCharacteristic added3 = bulletin.addCharacteristic(characteristic3);

        assertEquals(3, bulletin.getCharacteristics().size());

        // Act
        bulletin.removeCharacteristic(added2.getId());

        // Assert
        assertEquals(2, bulletin.getCharacteristics().size());
        assertTrue(bulletin.getCharacteristics().contains(added1));
        assertFalse(bulletin.getCharacteristics().contains(added2));
        assertTrue(bulletin.getCharacteristics().contains(added3));
    }

    @Test
    public void shouldAllowAddingSameCharacteristicAfterRemoval() {
        // Arrange
        Category category = createLeafyCategory();
        Bulletin bulletin = createBulletinWithSetCategory(category);
        Characteristic characteristic = createCharacteristic(category);

        BulletinCharacteristic added = bulletin.addCharacteristic(characteristic);
        assertEquals(1, bulletin.getCharacteristics().size());

        // Act
        bulletin.removeCharacteristic(added.getId());
        assertTrue(bulletin.getCharacteristics().isEmpty());

        // Act (again)
        BulletinCharacteristic addedAgain = bulletin.addCharacteristic(characteristic);

        // Assert
        assertEquals(1, bulletin.getCharacteristics().size());
        assertTrue(bulletin.getCharacteristics().contains(addedAgain));
    }

    private Bulletin createBulletin() {
        User user = User.createUser(UUID.randomUUID(), "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Bulletin.createDraft(ownerInfo);
    }

    private Bulletin createBulletinWithSetCategory(Category category) {
        Bulletin bulletin = createBulletin();
        bulletin.setCategory(category);
        return bulletin;
    }

    private Category createLeafyCategory() {
        Category root = Category.createRoot("root");
        return root.createLeafyChild("child 1");
    }

    private Characteristic createCharacteristic(Category category) {
        return category.addCharacteristic("characteristic");
    }

    private Characteristic createCharacteristicWithName(Category category, String name) {
        return category.addCharacteristic(name);
    }

}