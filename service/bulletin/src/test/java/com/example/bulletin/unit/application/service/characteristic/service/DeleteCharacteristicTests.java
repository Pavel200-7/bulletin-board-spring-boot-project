package com.example.bulletin.unit.application.service.characteristic.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.CharacteristicMapper;
import com.example.bulletin.application.service.category.data.request.DeleteCategoryRequest;
import com.example.bulletin.application.service.characteristic.CharacteristicServiceImpl;
import com.example.bulletin.application.service.characteristic.data.request.DeleteCharacteristicRequest;
import com.example.bulletin.application.service.characteristic.helper.inter.CharacteristicHierarchyPolicy;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import com.example.bulletin.infrastructure.repository.CategoryRepository;
import com.example.bulletin.infrastructure.repository.CharacteristicRepository;
import jakarta.validation.constraints.AssertTrue;
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
public class DeleteCharacteristicTests {

    @Autowired
    private CharacteristicMapper mapperHelper;

    @Mock
    private CharacteristicRepository characteristicRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CharacteristicHierarchyPolicy hierarchyPolicy;

    @Mock
    private CharacteristicMapper mapper;

    @InjectMocks
    private CharacteristicServiceImpl service;

    @Captor

    private ArgumentCaptor<Category> categoryCaptor;
    private Category category = null;

    @BeforeEach
    public void setup() {
        createCategory();
        Characteristic characteristic = createCharacteristic();
        when(characteristicRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(characteristic));
    }

    @Test
    public void shouldThrowWhenNotFound() {
        // Arrange
        DeleteCharacteristicRequest request = createRequest();
        when(characteristicRepository.findById(any(UUID.class)))
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

    public Characteristic createCharacteristic() {
        if(category.getCharacteristics().isEmpty()) {
            category.addCharacteristic("name");
        }
        return category.getCharacteristics().get(0);
    }

    public Category createCategory() {
        if (category == null) {
            category = Category.createRoot("root");
        }
        return category;
    }

    public DeleteCharacteristicRequest createRequest() {
        return DeleteCharacteristicRequest.builder()
                .id(UUID.randomUUID())
                .build();
    }

}
