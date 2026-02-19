package com.example.bulletin.unit.application.service.characteristicvalue.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.CharacteristicValueMapper;
import com.example.bulletin.application.service.characteristic_value.CharacteristicValueServiceImpl;
import com.example.bulletin.application.service.characteristic_value.data.request.GetCharacteristicValueRequest;
import com.example.bulletin.application.service.characteristic_value.data.response.data.CharacteristicValueResponse;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import com.example.bulletin.domain.entity.CharacteristicValue;
import com.example.bulletin.infrastructure.repository.CharacteristicRepository;
import com.example.bulletin.infrastructure.repository.CharacteristicValueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GetCharacteristicValueTests {

    @Autowired
    private CharacteristicValueMapper mapperHelper;

    @Mock
    private CharacteristicValueRepository characteristicValueRepository;

    @Mock
    private CharacteristicRepository characteristicRepository;

    @Mock
    private CharacteristicValueMapper mapper;

    @InjectMocks
    private CharacteristicValueServiceImpl service;

    @Captor
    private ArgumentCaptor<CharacteristicValue> characteristicValueCaptor;

    private Category category = null;
    private Characteristic characteristic = null;
    private CharacteristicValue characteristicValue = null;

    @BeforeEach
    public void setup() {
        when(characteristicValueRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(createCharacteristicValue()));

        when(mapper.toResponse(any(CharacteristicValue.class)))
                .thenAnswer(invocation -> {
                    CharacteristicValue valueToMap = invocation.getArgument(0);
                    return mapperHelper.toResponse(valueToMap);
                });
    }

    @Test
    public void shouldThrowWhenNotFound() {
        // Arrange
        GetCharacteristicValueRequest request = createRequest();
        when(characteristicValueRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {service.getCharacteristicValue(request); } );
    }

    @Test
    public void shouldReturnMappedCharacteristicValue() {
        // Arrange
        GetCharacteristicValueRequest request = createRequest();
        CharacteristicValueResponse expected = mapperHelper.toResponse(createCharacteristicValue());

        // Act
        var response = service.getCharacteristicValue(request);
        CharacteristicValueResponse actual = response.getCharacteristicValueResponse();

        // Assert
        assertTrue(expected.equalsData(actual));
    }

    public CharacteristicValue createCharacteristicValue() {
        if (characteristicValue == null) {
            Characteristic characteristic = createCharacteristic();
            characteristicValue = characteristic.addPossibleValue("test value");
        }
        return characteristicValue;
    }

    public Characteristic createCharacteristic() {
        if (characteristic == null) {
            Category category = createCategory();
            characteristic = category.addCharacteristic("characteristic");
        }
        return characteristic;
    }

    public Category createCategory() {
        if (category == null) {
            category = Category.createRoot("root");
        }
        return category;
    }

    public GetCharacteristicValueRequest createRequest() {
        return GetCharacteristicValueRequest.builder()
                .id(UUID.randomUUID())
                .build();
    }

}
