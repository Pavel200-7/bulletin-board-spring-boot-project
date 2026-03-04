package com.example.bulletin.unit.application.service.characteristic.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.CharacteristicMapper;
import com.example.bulletin.application.service.characteristic.CharacteristicServiceImpl;
import com.example.bulletin.application.service.characteristic.data.request.GetCharacteristicRequest;
import com.example.bulletin.application.data.response.CharacteristicResponse;
import com.example.bulletin.application.service.characteristic.helper.inter.CharacteristicHierarchyPolicy;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import com.example.bulletin.infrastructure.repository.CategoryRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GetCharacteristicTests {

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
        when(characteristicRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(createCharacteristic()));

        when(mapper.toResponse(any(Characteristic.class)))
                .thenAnswer(invocation -> {
                    Characteristic characteristicToMap = invocation.getArgument(0);
                    return mapperHelper.toResponse(characteristicToMap);
                });
    }

    @Test
    public void shouldThrowWhenNotFound() {
        // Arrange
        GetCharacteristicRequest request = createRequest();
        when(characteristicRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());


        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {service.getCharacteristic(request); } );
    }


    @Test
    public void shouldReturnMappedCharacteristic() {
        // Arrange
        GetCharacteristicRequest request = createRequest();
        CharacteristicResponse expected = mapperHelper.toResponse(createCharacteristic());

        // Act
        var response = service.getCharacteristic(request);
        CharacteristicResponse actual = response.getCharacteristicResponse();

        // Assert
        assertTrue(expected.equalsData(actual));
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

    public GetCharacteristicRequest createRequest() {
        return GetCharacteristicRequest.builder()
                .id(UUID.randomUUID())
                .build();
    }

}
