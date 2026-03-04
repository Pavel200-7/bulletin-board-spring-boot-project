package com.example.bulletin.unit.domain.entity.bulletin.update;

import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class BulletinSetCategoryTests {

    @Test
    public void shouldSetCategory() {
        // Arrange
        Bulletin bulletin = createBulletin();
        Category category = createLeafyCategory();

        // Act
        bulletin.setCategory(category);

        // Assert
        assertNotNull(bulletin.getCategory());
        assertEquals(category, bulletin.getCategory());
    }

    @Test
    public void shouldThrowWhenCategoryIsNotLeafy() {
        // Arrange
        Bulletin bulletin = createBulletin();
        Category category = createNotLeafyCategory();

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                bulletin.setCategory(category));
    }

    @Test
    public void shouldResetCharacteristicsAfterSetCategory() {
        // Arrange
        Category category = createLeafyCategory();

        Bulletin bulletin = createBulletin();
        bulletin.setCategory(category);
        bulletin.addCharacteristic(createCharacteristic(category));

        // Act
        bulletin.setCategory(category);

        // Assert
        assertTrue(bulletin.getCharacteristics().isEmpty());
    }

    private Bulletin createBulletin() {
        User user = User.createUser(UUID.randomUUID(), "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Bulletin.createDraft(ownerInfo);
    }

    private Category createLeafyCategory() {
        Category root = Category.createRoot("root");
        return root.createLeafyChild("child 1");
    }

    private Category createNotLeafyCategory() {
        return Category.createRoot("root");
    }

    private Characteristic createCharacteristic(Category category) {
        Characteristic characteristic = category.addCharacteristic("characteristic");
        return characteristic;
    }

}
