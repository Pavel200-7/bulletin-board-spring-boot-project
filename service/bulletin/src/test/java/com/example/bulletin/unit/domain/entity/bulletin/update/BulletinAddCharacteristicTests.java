package com.example.bulletin.unit.domain.entity.bulletin.update;

import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class BulletinAddCharacteristicTests {

    @Test
    public void shouldAddCharacteristic()
            throws AccessDeniedException {
        // Arrange
        Category category = createLeafyCategory();
        Bulletin bulletin = createBulletinWithSetCategory(category);

        Characteristic characteristic = createCharacteristic(category);

        // Act
        bulletin.addCharacteristic(characteristic);

        // Assert
        assertFalse(bulletin.getCharacteristics().isEmpty());
    }

    @Test
    public void shouldThrowWhenCategoryIsNull()
            throws AccessDeniedException {
        // Arrange
        Category category = createLeafyCategory();
        Bulletin bulletin = createBulletin();

        Characteristic characteristic = createCharacteristic(category);

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                bulletin.addCharacteristic(characteristic));
    }

    @Test
    public void shouldThrowWhenCharacteristicIsNotOfCategory()
            throws AccessDeniedException {
        // Arrange
        Category category = createLeafyCategory();
        Bulletin bulletin = createBulletinWithSetCategory(category);

        Category anotherCategory = createLeafyCategory();
        Characteristic characteristic = createCharacteristic(anotherCategory);

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                bulletin.addCharacteristic(characteristic));
    }

    @Test
    public void shouldThrowWhenCharacteristicIsNotUnique()
            throws AccessDeniedException {
        // Arrange
        Category category = createLeafyCategory();
        Bulletin bulletin = createBulletinWithSetCategory(category);
        Characteristic characteristic = createCharacteristic(category);
        bulletin.addCharacteristic(characteristic);

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                bulletin.addCharacteristic(characteristic));
    }

    private Bulletin createBulletin()
            throws AccessDeniedException {
        User user = User.createUser(UUID.randomUUID(), "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        Bulletin bulletin = Bulletin.createDraft(ownerInfo);
        return bulletin;
    }

    private Bulletin createBulletinWithSetCategory(Category category)
            throws AccessDeniedException {
        Bulletin bulletin = createBulletin();
        bulletin.setCategory(category);
        return bulletin;
    }

    private Category createLeafyCategory() {
        Category root = Category.createRoot("root");
        return root.createLeafyChild("child 1");
    }

    private Characteristic createCharacteristic(Category category) {
        Characteristic characteristic = category.addCharacteristic("characteristic");
        return characteristic;
    }

}
