package com.example.bulletin.unit.application.mapper;

import com.example.bulletin.application.mapper.*;
import com.example.bulletin.domain.entity.*;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.domain.vo.BulletinCharacteristicData;
import com.example.bulletin.domain.vo.CharacteristicData;
import com.example.bulletin.domain.vo.CharacteristicValueData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class BulletinCharacteristicMapperTests {

    @Autowired
    private BulletinCharacteristicMapper mapper;

    @Autowired
    private CharacteristicMapper characteristicMapper;

    @Autowired
    private CharacteristicValueMapper characteristicValueMapper;



    @Test
    public void shouldConvertCorrectlyFromEntityToData() {
        // Arrange
        BulletinCharacteristic bulletinCharacteristic = createBulletinCharacteristic();

        CharacteristicData characteristicData = characteristicMapper.toData(bulletinCharacteristic.getName());
        CharacteristicValueData valueData = characteristicValueMapper.toData(bulletinCharacteristic.getValue());

        BulletinCharacteristicData expected = BulletinCharacteristicData.builder()
                .id(bulletinCharacteristic.getId())
                .bulletinId(bulletinCharacteristic.getBulletin().getId())
                .name(characteristicData)
                .value(valueData)
                .build();

        // Act
        BulletinCharacteristicData actual = mapper.toData(bulletinCharacteristic);

        // Assert
        assertNotNull(actual);
        assertTrue(expected.equalsData(actual));
    }

    @Test
    public void shouldHandleNullValue() {
        // Arrange
        BulletinCharacteristic bulletinCharacteristic = createBulletinCharacteristicWithNullValue();

        CharacteristicData characteristicData = characteristicMapper.toData(bulletinCharacteristic.getName());

        BulletinCharacteristicData expected = BulletinCharacteristicData.builder()
                .id(bulletinCharacteristic.getId())
                .bulletinId(bulletinCharacteristic.getBulletin().getId())
                .name(characteristicData)
                .value(null)
                .build();

        // Act
        BulletinCharacteristicData actual = mapper.toData(bulletinCharacteristic);

        // Assert
        assertNotNull(actual);
        assertTrue(expected.equalsData(actual));
    }

    private BulletinCharacteristic createBulletinCharacteristic() {
        Category category = Category.createRoot("test");
        Characteristic characteristic = category.addCharacteristic("test characteristic");
        CharacteristicValue value = characteristic.addPossibleValue("test value");

        User user = createUser();
        OwnerInfo ownerInfo = new OwnerInfo(user);
        Bulletin bulletin = Bulletin.createDraft(ownerInfo);

        BulletinCharacteristic bulletinCharacteristic = BulletinCharacteristic.createBulletinCharacteristic(bulletin, characteristic);
        bulletinCharacteristic.setValue(value);

        return bulletinCharacteristic;
    }

    private BulletinCharacteristic createBulletinCharacteristicWithNullValue() {
        Category category = Category.createRoot("test");
        Characteristic characteristic = category.addCharacteristic("test characteristic");

        User user = createUser();
        OwnerInfo ownerInfo = new OwnerInfo(user);
        Bulletin bulletin = Bulletin.createDraft(ownerInfo);

        BulletinCharacteristic bulletinCharacteristic = BulletinCharacteristic.createBulletinCharacteristic(bulletin, characteristic);

        return bulletinCharacteristic;
    }

    private User createUser() {
        return User.createUser(UUID.randomUUID(), "test@example.com");
    }

}