package com.example.bulletin.unit.domain.entity.bulletincharacteristic;

import com.example.bulletin.application.mapper.BulletinCharacteristicMapper;
import com.example.bulletin.application.mapper.CharacteristicMapper;
import com.example.bulletin.application.mapper.CharacteristicValueMapper;
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
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
public class BulletinCharacteristicUpdateTests {

    @Autowired
    private BulletinCharacteristicMapper bulletinCharacteristicMapper;

    @Autowired
    private CharacteristicMapper characteristicMapper;

    @Autowired
    private CharacteristicValueMapper characteristicValueMapper;

    private Category categoryAggregate = null;

    @Test
    public void shouldSetBulletinCharacteristicValue()
            throws AccessDeniedException {
        // Arrange
        Bulletin bulletin = createBulletin();
        Category category = createCategoryAggregate();
        Characteristic characteristic = category.getCharacteristics().get(0);
        CharacteristicValue characteristicValue = characteristic.getPossibleValues().get(0);

        BulletinCharacteristic bulletinCharacteristic = BulletinCharacteristic.createBulletinCharacteristic(bulletin, characteristic);


        BulletinCharacteristicData expected = BulletinCharacteristicData.builder()
                .id(UUID.randomUUID())
                .bulletinId(bulletin.getId())
                .name(characteristicMapper.toData(characteristic))
                .value(characteristicValueMapper.toData(characteristicValue))
                .build();

        // Act
        bulletinCharacteristic = bulletinCharacteristic.setValue(characteristicValue);
        BulletinCharacteristicData actual = bulletinCharacteristicMapper.toData(bulletinCharacteristic);

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    public void shouldThrowWhenValueIsOfAnotherCharacteristic()
            throws AccessDeniedException {
        // Arrange
        Bulletin bulletin = createBulletin();
        Category category = createCategoryAggregate();
        Characteristic characteristic = category.getCharacteristics().get(0);

        Characteristic anotherCharacteristic = category.addCharacteristic("another characteristic");
        CharacteristicValue anotherCharacteristicValue = anotherCharacteristic.addPossibleValue("another characteristic value");


        BulletinCharacteristic bulletinCharacteristic = BulletinCharacteristic.createBulletinCharacteristic(bulletin, characteristic);

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
