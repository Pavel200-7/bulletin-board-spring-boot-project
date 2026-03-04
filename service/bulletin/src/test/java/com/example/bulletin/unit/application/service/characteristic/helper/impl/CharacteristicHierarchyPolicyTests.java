package com.example.bulletin.unit.application.service.characteristic.helper.impl;

import com.example.bulletin.application.service.characteristic.helper.impl.CharacteristicHierarchyPolicyImpl;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.infrastructure.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CharacteristicHierarchyPolicyTests {

    @Mock
    private CategoryRepository repository;

    @InjectMocks
    private CharacteristicHierarchyPolicyImpl hierarchyPolicy;

    @BeforeEach
    public void setup() {
        when(repository.existsCharacteristicWithNameInHierarchy(any(UUID.class), any(String.class)))
                .thenReturn(false);
    }

    @Test
    public void shouldThrowWhenNameIsNotUniqueInHierarchy() {
        // Arrange
        Category category = Category.createRoot("root");
        String characteristicName = "name";

        when(repository.existsCharacteristicWithNameInHierarchy(any(UUID.class), any(String.class)))
                .thenReturn(true);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> { hierarchyPolicy.enforceAddingRules(category, characteristicName); });
    }
}
