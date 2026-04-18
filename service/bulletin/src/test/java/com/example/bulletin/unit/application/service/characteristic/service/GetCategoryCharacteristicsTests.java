package com.example.bulletin.unit.application.service.characteristic.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.CharacteristicMapper;
import com.example.bulletin.application.service.characteristic.CharacteristicServiceImpl;
import com.example.bulletin.application.service.characteristic.data.request.GetCategoryCharacteristicsRequest;
import com.example.bulletin.application.data.response.CharacteristicResponse;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import com.example.bulletin.infrastructure.repository.CategoryRepository;
import com.example.bulletin.infrastructure.repository.CharacteristicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GetCategoryCharacteristicsTests {

    private CharacteristicMapper mapperHelper = Mappers.getMapper(
            CharacteristicMapper.class);

    @Mock
    private CharacteristicRepository characteristicRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CharacteristicMapper mapper;

    @InjectMocks
    private CharacteristicServiceImpl service;

    private Category category = null;
    private Characteristic characteristic = null;

    @BeforeEach
    public void setup() {
        when(categoryRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(createCategory()));

        List<Characteristic> characteristics = new ArrayList<>();
        characteristics.add(createCharacteristic());

        when(characteristicRepository.findByCategoryId(any(UUID.class)))
                .thenReturn(characteristics);

        when(mapper.toResponse(any(Characteristic.class)))
                .thenAnswer(invocation -> {
                    Characteristic characteristicToMap = invocation.getArgument(0);
                    return mapperHelper.toResponse(characteristicToMap);
                });
    }

    @Test
    public void shouldThrowWhenCategoryNotFound() {
        // Arrange
        GetCategoryCharacteristicsRequest request = createRequest();
        when(categoryRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> { service.getCategoryCharacteristics(request); });
    }

    @Test
    public void shouldGetCategoryHierarchyCharacteristic() {
        // Arrange
        GetCategoryCharacteristicsRequest request = createRequest();

        // Act
        service.getCategoryCharacteristics(request);

        // Assert
        verify(characteristicRepository, Mockito.times(1)).findByCategoryId(request.getCategoryId());

    }


    @Test
    public void shouldMapBeforeReturn() {
        // Arrange
        GetCategoryCharacteristicsRequest request = createRequest();

        CharacteristicResponse characteristicResponse = mapperHelper.toResponse(createCharacteristic());
        List<CharacteristicResponse> expected = new ArrayList<>();
        expected.add(characteristicResponse);


        // Act
        var response = service.getCategoryCharacteristics(request);
        List<CharacteristicResponse> actual = response.getCharacteristicResponse();

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    public Characteristic createCharacteristic() {
        if (characteristic == null) {
            Category category = createCategory();
            characteristic = category.addCharacteristic("old name");
        }
        return characteristic;
    }

    public Category createCategory() {
        if (category == null) {
            category = Category.createRoot("root");
        }
        return category;
    }

    public GetCategoryCharacteristicsRequest createRequest() {
        return GetCategoryCharacteristicsRequest.builder()
                .categoryId(UUID.randomUUID())
                .build();
    }
}
