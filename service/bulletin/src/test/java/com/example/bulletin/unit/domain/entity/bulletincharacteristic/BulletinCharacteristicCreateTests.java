package com.example.bulletin.unit.domain.entity.bulletincharacteristic;

import com.example.bulletin.application.mapper.BulletinCharacteristicMapper;
import com.example.bulletin.application.mapper.CharacteristicMapper;
import com.example.bulletin.domain.entity.*;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.domain.vo.BulletinCharacteristicData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class BulletinCharacteristicCreateTests {

    @Autowired
    private BulletinCharacteristicMapper bulletinCharacteristicMapper;

    @Autowired
    private CharacteristicMapper characteristicMapper;

    private Category categoryAggregate = null;

    @Test
    public void shouldCreateBulletinCharacteristic() throws AccessDeniedException {
        // Arrange
        Bulletin bulletin = createBulletin();
        Category category = createCategoryAggregate();
        Characteristic characteristic = category.getCharacteristics().get(0);
        CharacteristicValue characteristicValue = characteristic.getPossibleValues().get(0);

        BulletinCharacteristicData expected = BulletinCharacteristicData.builder()
                .id(UUID.randomUUID())
                .bulletinId(bulletin.getId())
                .name(characteristicMapper.toData(characteristic))
                .value(null)
                .build();

        // Act
        BulletinCharacteristic bulletinCharacteristic = BulletinCharacteristic.createBulletinCharacteristic(bulletin, characteristic);
        BulletinCharacteristicData actual = bulletinCharacteristicMapper.toData(bulletinCharacteristic);

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);

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
