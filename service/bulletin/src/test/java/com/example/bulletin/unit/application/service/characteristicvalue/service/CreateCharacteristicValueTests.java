package com.example.bulletin.unit.application.service.characteristicvalue.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.CharacteristicValueMapper;
import com.example.bulletin.application.service.characteristic_value.CharacteristicValueServiceImpl;
import com.example.bulletin.application.service.characteristic_value.data.request.CreateCharacteristicValueRequest;
import com.example.bulletin.application.service.characteristic_value.data.response.CreateCharacteristicValueResponse;
import com.example.bulletin.application.service.characteristic_value.data.response.data.CharacteristicValueResponse;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import com.example.bulletin.domain.entity.CharacteristicValue;
import com.example.bulletin.infrastructure.repository.CharacteristicRepository;
import com.example.bulletin.infrastructure.repository.CharacteristicValueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CreateCharacteristicValueTests {

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
        when(characteristicRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(createCharacteristic()));

        when(mapper.toResponse(any(CharacteristicValue.class)))
                .thenReturn(mapperHelper.toResponse(createCharacteristicValue()));
    }

    @Test
    public void shouldThrowWhenCharacteristicNotFound() {
        // Arrange
        CreateCharacteristicValueRequest request = createRequest();
        when(characteristicRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> { service.createCharacteristicValue(request); });
    }

    @Test
    public void shouldCreateCharacteristicValueAndSave() {
        // Arrange
        CreateCharacteristicValueRequest request = createRequest();
        CharacteristicValue expected = createCharacteristicValue();

        // Act
        service.createCharacteristicValue(request);

        // Assert
        verify(characteristicValueRepository).save(characteristicValueCaptor.capture());
        CharacteristicValue actual = characteristicValueCaptor.getValue();

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    public void shouldMapBeforeReturn() {
        // Arrange
        CreateCharacteristicValueRequest request = createRequest();
        CharacteristicValueResponse expected = mapperHelper.toResponse(createCharacteristicValue());

        // Act
        CreateCharacteristicValueResponse response = service.createCharacteristicValue(request);
        CharacteristicValueResponse actual = response.getCharacteristicValueResponse();

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    public CharacteristicValue createCharacteristicValue() {
        if (characteristicValue == null) {
            CreateCharacteristicValueRequest request = createRequest();
            Characteristic characteristic = createCharacteristic();
            characteristicValue = characteristic.addPossibleValue(request.getName());
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

    public CreateCharacteristicValueRequest createRequest() {
        return CreateCharacteristicValueRequest.builder()
                .characteristicId(UUID.randomUUID())
                .name("test value")
                .build();
    }

}