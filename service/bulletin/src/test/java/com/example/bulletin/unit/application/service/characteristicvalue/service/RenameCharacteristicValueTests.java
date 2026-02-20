package com.example.bulletin.unit.application.service.characteristicvalue.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.CharacteristicValueMapper;
import com.example.bulletin.application.service.characteristicvalue.CharacteristicValueServiceImpl;
import com.example.bulletin.application.service.characteristicvalue.data.request.RenameCharacteristicValueRequest;
import com.example.bulletin.application.service.characteristicvalue.data.response.data.CharacteristicValueResponse;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RenameCharacteristicValueTests {

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
        RenameCharacteristicValueRequest request = createRequest();
        when(characteristicValueRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {service.renameCharacteristicValue(request); } );
    }

    @Test
    public void shouldRenameAndSave() {
        // Arrange
        RenameCharacteristicValueRequest request = createRequest();
        CharacteristicValue expected = createCharacteristicValue()
                .rename(request.getName());

        // Act
        service.renameCharacteristicValue(request);

        // Assert
        verify(characteristicValueRepository).save(characteristicValueCaptor.capture());
        CharacteristicValue actual = characteristicValueCaptor.getValue();

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    public void shouldReturnMappedCharacteristicValue() {
        // Arrange
        RenameCharacteristicValueRequest request = createRequest();

        CharacteristicValue renamedValue = createCharacteristicValue()
                .rename(request.getName());
        CharacteristicValueResponse expected = mapperHelper.toResponse(renamedValue);

        // Act
        var response = service.renameCharacteristicValue(request);
        CharacteristicValueResponse actual = response.getCharacteristicValueResponse();

        // Assert
        assertTrue(expected.equalsData(actual));
    }

    public CharacteristicValue createCharacteristicValue() {
        if (characteristicValue == null) {
            Characteristic characteristic = createCharacteristic();
            characteristicValue = characteristic.addPossibleValue("old value");
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

    public RenameCharacteristicValueRequest createRequest() {
        return RenameCharacteristicValueRequest.builder()
                .id(UUID.randomUUID())
                .name("new value")
                .build();
    }

}