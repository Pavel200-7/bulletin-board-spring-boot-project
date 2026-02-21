package com.example.bulletin.unit.domain.entity.characteristic;

import com.example.bulletin.application.mapper.CharacteristicMapper;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import com.example.bulletin.domain.entity.CharacteristicValue;
import com.example.bulletin.domain.vo.CharacteristicData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CharacteristicUpdateTests {

    @Autowired
    private CharacteristicMapper mapper;

    @Test
    public void shouldRename() {
        // Arrange
        Category category = Category.createRoot("Name 1");
        Characteristic characteristic = category.addCharacteristic("characteristic");
        String newName = "new name";
        CharacteristicData expected = CharacteristicData.builder()
                .name(newName)
                .categoryId(category.getId())
                .build();

        // Act
        Characteristic renamedCharacteristic = characteristic.rename(newName);
        CharacteristicData actual = mapper.toData(renamedCharacteristic);

        // Assert
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    public void shouldAddPossibleValue() {
        // Arrange
        Category category = Category.createRoot("root");
        Characteristic characteristic = category.addCharacteristic("characteristic 1");
        String valueName = "possible value 1";

        // Act
        characteristic.addPossibleValue(valueName);

        // Assert
        assertFalse(characteristic.getPossibleValues().isEmpty());
        assertEquals(valueName,
                characteristic.getPossibleValues().get(0).getName());
    }

    @Test
    public void shouldRemovePossibleValue() {
        // Arrange
        Category category = Category.createRoot("root");
        Characteristic characteristic = category.addCharacteristic("characteristic 1");
        String valueName = "possible value 1";
        CharacteristicValue value = characteristic.addPossibleValue(valueName);


        // Act
        characteristic.removePossibleValue(value);


        // Assert
        assertTrue(characteristic.getPossibleValues().isEmpty());
    }

    @Test
    public void shouldThrowWhenPossibleValueIsOfAnotherCharacteristic() {
        // Arrange
        Category category = Category.createRoot("root");
        Characteristic characteristic = category.addCharacteristic("characteristic 1");

        Characteristic anotherCharacteristic = category.addCharacteristic("another characteristic");
        String anotherValueName = "possible value 1";
        CharacteristicValue anotherCharacteristicValue = anotherCharacteristic.addPossibleValue(anotherValueName);

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                characteristic.removePossibleValue(anotherCharacteristicValue));
    }

}
