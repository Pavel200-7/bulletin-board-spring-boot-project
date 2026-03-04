package com.example.bulletin.unit.application.service.characteristicvalue.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.CharacteristicValueMapper;
import com.example.bulletin.application.service.characteristicvalue.CharacteristicValueServiceImpl;
import com.example.bulletin.application.service.characteristicvalue.data.request.GetCharacteristicValuesRequest;
import com.example.bulletin.application.data.response.CharacteristicValueResponse;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GetCharacteristicValuesTests {

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

        List<CharacteristicValue> characteristicValues = new ArrayList<>();
        characteristicValues.add(createCharacteristicValue());
        when(characteristicValueRepository.findByCharacteristicId(any(UUID.class)))
                .thenReturn(characteristicValues);

        when(mapper.toResponse(any(CharacteristicValue.class)))
                .thenAnswer(invocation -> {
                    CharacteristicValue valueToMap = invocation.getArgument(0);
                    return mapperHelper.toResponse(valueToMap);
                });
    }

    @Test
    public void shouldThrowWhenCharacteristicNotFound() {
        // Arrange
        GetCharacteristicValuesRequest request = createRequest();
        when(characteristicRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> { service.getCharacteristicValues(request); });
    }

    @Test
    public void shouldMapBeforeReturn() {
        // Arrange
        GetCharacteristicValuesRequest request = createRequest();

        CharacteristicValueResponse valueResponse = mapperHelper.toResponse(createCharacteristicValue());
        List<CharacteristicValueResponse> expected = new ArrayList<>();
        expected.add(valueResponse);

        // Act
        var response = service.getCharacteristicValues(request);
        List<CharacteristicValueResponse> actual = response.getCharacteristicValueResponse();

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
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

    public GetCharacteristicValuesRequest createRequest() {
        return GetCharacteristicValuesRequest.builder()
                .characteristicId(UUID.randomUUID())
                .build();
    }

}