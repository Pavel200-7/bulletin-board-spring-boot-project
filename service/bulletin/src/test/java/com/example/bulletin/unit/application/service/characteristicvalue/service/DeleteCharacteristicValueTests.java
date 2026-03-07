package com.example.bulletin.unit.application.service.characteristicvalue.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.service.characteristicvalue.CharacteristicValueServiceImpl;
import com.example.bulletin.application.service.characteristicvalue.data.request.DeleteCharacteristicValueRequest;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import com.example.bulletin.domain.entity.CharacteristicValue;
import com.example.bulletin.infrastructure.repository.CharacteristicRepository;
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
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DeleteCharacteristicValueTests {

    @Mock
    private CharacteristicRepository characteristicRepository;

    @InjectMocks
    private CharacteristicValueServiceImpl service;

    private Characteristic characteristic = null;

    @BeforeEach
    public void setup() {

        createCharacteristic();
        CharacteristicValue characteristicValue = createCharacteristicValue();
        when(characteristicRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(characteristic));
    }

    @Test
    public void shouldThrowWhenNotFound() {
        // Arrange
        DeleteCharacteristicValueRequest request = createRequest();
        when(characteristicRepository.findById(any(UUID.class)))
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

    private CharacteristicValue createCharacteristicValue() {
        if (characteristic.getPossibleValues().isEmpty()) {
            characteristic.addPossibleValue("test value");
        }
        return characteristic.getPossibleValues()
                .getFirst();
    }

    private Characteristic createCharacteristic() {
        if (characteristic == null) {
            Category category = Category.createRoot("root");
            characteristic = category.addCharacteristic("characteristic");
        }
        return characteristic;
    }

    private DeleteCharacteristicValueRequest createRequest() {
        UUID characteristicValueId = createCharacteristicValue().getId();
        return DeleteCharacteristicValueRequest.builder()
                .characteristicId(UUID.randomUUID())
                .characteristicValueId(characteristicValueId)
                .build();
    }

}