package com.example.bulletin.unit.domain.entity.bulletin.update;

import com.example.bulletin.application.mapper.BulletinMapper;
import com.example.bulletin.application.mapper.CategoryMapper;
import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
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
public class BulletinSetCategoryTests {

    @Autowired
    private BulletinMapper mapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    public void shouldSetCategory()
            throws AccessDeniedException {
        // Arrange
        Bulletin bulletin = createBulletin();
        Category category = createLeafyCategory();
        BulletinData expected = createExpectedBuilder(bulletin, category).build();

        // Act
        bulletin = bulletin.setCategory(category);
        BulletinData actual = mapper.toData(bulletin);

        // Assert
        assertTrue(expected.equalsData(actual));
    }

    @Test
    public void shouldThrowWhenCategoryIsNotLeafy()
            throws AccessDeniedException {
        // Arrange
        Bulletin bulletin = createBulletin();
        Category category = createNotLeafyCategory();
        BulletinData expected = createExpectedBuilder(bulletin, category).build();

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                bulletin.setCategory(category));
    }

    @Test
    public void shouldResetCharacteristicsAfterSetCategory()
            throws AccessDeniedException {
        // Arrange
        Category category = createLeafyCategory();

        Bulletin bulletin = createBulletin();
        bulletin.setCategory(category);
        bulletin.addCharacteristic(createCharacteristic(category));
        BulletinData expected = createExpectedBuilder(bulletin, category).build();

        // Act
        bulletin = bulletin.setCategory(category);
        BulletinData actual = mapper.toData(bulletin);

        // Assert
        assertTrue(expected.equalsData(actual));
    }

    private BulletinData.BulletinDataBuilder createExpectedBuilder(Bulletin bulletin, Category category) {
        return BulletinData.builder()
                .id(UUID.randomUUID())
                .ownerId(bulletin.getOwnerInfo().getOwnerId())
                .title(null)
                .description(null)
                .price(0)
                .rating(0)
                .status(BulletinStatus.DRAFT)
                .category(categoryMapper.toData(category))
                .characteristics(List.of())
                .images(List.of());
    }

    private Bulletin createBulletin()
            throws AccessDeniedException {
        User user = User.createUser(UUID.randomUUID(), "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Bulletin.createDraft(ownerInfo);
    }

    public Category createLeafyCategory() {
        Category root = Category.createRoot("root");
        return root.createLeafyChild("child 1");
    }

    public Category createNotLeafyCategory() {
        return Category.createRoot("root");
    }

    public Characteristic createCharacteristic(Category category) {
        Characteristic characteristic = category.addCharacteristic("characteristic");
        return characteristic;
    }

}
