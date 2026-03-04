package com.example.bulletin.unit.application.service.characteristic.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.CharacteristicMapper;
import com.example.bulletin.application.service.characteristic.CharacteristicServiceImpl;
import com.example.bulletin.application.service.characteristic.data.request.CreateCharacteristicRequest;
import com.example.bulletin.application.service.characteristic.data.response.CreateCharacteristicResponse;
import com.example.bulletin.application.data.response.CharacteristicResponse;
import com.example.bulletin.application.service.characteristic.helper.inter.CharacteristicHierarchyPolicy;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import com.example.bulletin.infrastructure.repository.CategoryRepository;
import com.example.bulletin.infrastructure.repository.CharacteristicRepository;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CreateCharacteristicTests {

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
    private Characteristic characteristic = null;


    @BeforeEach
    public void setup() {
        when(categoryRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(createCategory()));

        when(mapper.toResponse(any(Characteristic.class)))
                .thenReturn(mapperHelper.toResponse(createCharacteristic()));
    }

    @Test
    public void shouldThrowWhenCategoryNotFound() {
        // Arrange
        CreateCharacteristicRequest request = createRequest();
        when(categoryRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> { service.createCharacteristic(request); });
    }

    @Test
    public void shouldCallHierarchyPolicy() {
        // Arrange
        CreateCharacteristicRequest request = createRequest();

        // Act
        service.createCharacteristic(request);

        // Assert
        verify(hierarchyPolicy, Mockito.times(1)).enforceAddingRules(createCategory(), request.getName());
    }

    @Test
    public void shouldCreateCharacteristicAndSave() {
        // Arrange
        CreateCharacteristicRequest request = createRequest();
        Characteristic expected = createCharacteristic();
        // Act
        service.createCharacteristic(request);

        // Assert
        verify(categoryRepository).save(categoryCaptor.capture());
        Characteristic actual = categoryCaptor.getValue()
                .getCharacteristics().get(0);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    public void shouldMapBeforeReturn() {
        // Arrange
        CreateCharacteristicRequest request = createRequest();
        CharacteristicResponse expected = mapperHelper.toResponse(createCharacteristic());

        // Act
        CreateCharacteristicResponse response = service.createCharacteristic(request);
        CharacteristicResponse actual = response.getCharacteristicResponse();

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    public Characteristic createCharacteristic() {
        if (characteristic == null) {
            CreateCharacteristicRequest request = createRequest();
            Category category = createCategory();
            characteristic = category.addCharacteristic(request.getName());
        }
        return characteristic;
    }

    public Category createCategory() {
        if (category == null) {
            category = Category.createRoot("root");
        }
        return category;
    }

    public CreateCharacteristicRequest createRequest() {
        return CreateCharacteristicRequest.builder()
                .categoryId(UUID.randomUUID())
                .name("characteristic")
                .build();
    }

}
