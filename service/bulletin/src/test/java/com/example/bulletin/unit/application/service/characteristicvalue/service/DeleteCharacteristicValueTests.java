package com.example.bulletin.unit.application.service.characteristicvalue.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.CharacteristicValueMapper;
import com.example.bulletin.application.service.characteristicvalue.CharacteristicValueServiceImpl;
import com.example.bulletin.application.service.characteristicvalue.data.request.DeleteCharacteristicValueRequest;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DeleteCharacteristicValueTests {

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

    private Characteristic characteristic = null;

    @BeforeEach
    public void setup() {

        createCharacteristic();
        CharacteristicValue characteristicValue = createCharacteristicValue();
        when(characteristicValueRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(characteristicValue));
    }

    @Test
    public void shouldThrowWhenNotFound() {
        // Arrange
        DeleteCharacteristicValueRequest request = createRequest();
        when(characteristicValueRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {service.deleteCharacteristicValue(request); } );
    }

    @Test
    public void shouldDelete() {
        // Arrange
        DeleteCharacteristicValueRequest request = createRequest();

        // Act
        service.deleteCharacteristicValue(request);

        // Assert
        assertTrue(characteristic.getPossibleValues().isEmpty());
        verify(characteristicRepository).save(characteristic);
    }

    public CharacteristicValue createCharacteristicValue() {
        if (characteristic.getPossibleValues().isEmpty()) {
            characteristic.addPossibleValue("test value");
        }
        return characteristic.getPossibleValues().get(0);
    }

    public Characteristic createCharacteristic() {
        if (characteristic == null) {
            Category category = Category.createRoot("root");
            characteristic = category.addCharacteristic("characteristic");
        }
        return characteristic;
    }

    public DeleteCharacteristicValueRequest createRequest() {
        return DeleteCharacteristicValueRequest.builder()
                .id(UUID.randomUUID())
                .build();
    }

}