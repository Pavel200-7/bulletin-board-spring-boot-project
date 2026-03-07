package com.example.bulletin.unit.application.mapper;

import com.example.bulletin.application.mapper.CharacteristicMapper;
import com.example.bulletin.application.data.response.CharacteristicResponse;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import com.example.bulletin.domain.vo.CharacteristicData;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class CharacteristicMapperTests {

    private CharacteristicMapper mapper = Mappers.getMapper(
            CharacteristicMapper.class);

    @Test
    public void shouldConvertCorrectlyFromEntityToData() {
        // Arrange
        Characteristic characteristic = createCharacteristic();
        CharacteristicData expected = CharacteristicData.builder()
                .id(characteristic.getId())
                .name(characteristic.getName())
                .categoryId(characteristic.getCategory().getId())
                .build();

        // Act
        CharacteristicData actual = mapper.toData(characteristic);

        // Assert
        assertTrue(expected.equals(actual));
    }

    @Test
    public void shouldConvertCorrectlyFromEntityToResponse() {
        // Arrange
        Characteristic characteristic = createCharacteristic();
        CharacteristicResponse expected = CharacteristicResponse.builder()
                .id(characteristic.getId())
                .name(characteristic.getName())
                .categoryId(characteristic.getCategory().getId())
                .build();

        // Act
        CharacteristicResponse actual = mapper.toResponse(characteristic);

        // Assert
        assertTrue(expected.equals(actual));
    }

    private Characteristic createCharacteristic() {
        Category category = Category.createRoot("test");
        category.addCharacteristic("characteristic");
        return category.getCharacteristics().get(0);
    }

}
