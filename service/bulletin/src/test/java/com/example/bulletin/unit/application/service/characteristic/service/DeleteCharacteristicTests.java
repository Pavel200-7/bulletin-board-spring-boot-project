package com.example.bulletin.unit.application.service.characteristic.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.CharacteristicMapper;
import com.example.bulletin.application.service.characteristic.CharacteristicServiceImpl;
import com.example.bulletin.application.service.characteristic.data.request.DeleteCharacteristicRequest;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import com.example.bulletin.infrastructure.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
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
public class DeleteCharacteristicTests {

    private CharacteristicMapper mapperHelper = Mappers.getMapper(
            CharacteristicMapper.class);

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CharacteristicServiceImpl service;

    private Category category = null;

    @BeforeEach
    public void setup() {
        createCategory();
        Characteristic characteristic = createCharacteristic();
        when(categoryRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(category));
    }

    @Test
    public void shouldThrowWhenNotFound() {
        // Arrange
        DeleteCharacteristicRequest request = createRequest();
        when(categoryRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {service.deleteCharacteristic(request); } );
    }

    @Test
    public void shouldDelete() {
        // Arrange
        DeleteCharacteristicRequest request = createRequest();

        // Act
        service.deleteCharacteristic(request);

        // Assert
        assertTrue(category.getCharacteristics().isEmpty());
        verify(categoryRepository).save(category);
    }

    private Characteristic createCharacteristic() {
        if(category.getCharacteristics().isEmpty()) {
            category.addCharacteristic("name");
        }
        return category.getCharacteristics()
                .getFirst();
    }

    private Category createCategory() {
        if (category == null) {
            category = Category.createRoot("root");
        }
        return category;
    }

    private DeleteCharacteristicRequest createRequest() {
        UUID characteristicId = createCharacteristic().getId();
        return DeleteCharacteristicRequest.builder()
                .categoryId(UUID.randomUUID())
                .characteristicId(characteristicId)
                .build();
    }

}
