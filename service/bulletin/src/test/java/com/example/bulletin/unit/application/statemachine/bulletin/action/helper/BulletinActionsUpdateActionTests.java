package com.example.bulletin.unit.application.statemachine.bulletin.action.helper;

import com.example.bulletin.application.data.request.BulletinCharacteristicRequest;
import com.example.bulletin.application.data.request.BulletinRequest;
import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.statemachine.bulletin.action.helper.BulletinModifyServiceImpl;
import com.example.bulletin.domain.entity.*;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.infrastructure.repository.CategoryRepository;
import com.example.bulletin.infrastructure.repository.CharacteristicRepository;
import com.example.bulletin.infrastructure.repository.CharacteristicValueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BulletinActionsUpdateActionTests {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CharacteristicRepository characteristicRepository;

    @Mock
    private CharacteristicValueRepository characteristicValueRepository;

    @InjectMocks
    private BulletinModifyServiceImpl modifyService;

    private Bulletin bulletin;
    private Category leafCategory;
    private Category rootCategory;
    private Characteristic characteristic1;
    private Characteristic characteristic2;
    private Characteristic characteristic3;
    private CharacteristicValue value1;
    private CharacteristicValue value2;
    private CharacteristicValue value3;
    private CharacteristicValue newValue1;

    @BeforeEach
    void setUp() {
        User user = User.createUser(UUID.randomUUID(), "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);

        rootCategory = Category.createRoot("Root Category");
        leafCategory = rootCategory.createLeafyChild("Leaf Category");

        bulletin = Bulletin.createDraft(ownerInfo);

        characteristic1 = leafCategory.addCharacteristic("Color");
        characteristic2 = leafCategory.addCharacteristic("Size");
        characteristic3 = leafCategory.addCharacteristic("Material");

        value1 = characteristic1.addPossibleValue("Red");
        value2 = characteristic2.addPossibleValue("Large");
        value3 = characteristic3.addPossibleValue("Wood");
        newValue1 = characteristic1.addPossibleValue("Blue");
    }

    @Test
    void shouldUpdateSimpleFields() {
        // Arrange
        bulletin.setCategory(leafCategory);

        BulletinRequest request = BulletinRequest.builder()
                .title("New Title")
                .description("New Description")
                .price(999.99)
                .categoryId(leafCategory.getId())
                .characteristics(new ArrayList<>())
                .build();

        // Act
        modifyService.updateBulletin(bulletin, request);

        // Assert
        assertEquals("New Title", bulletin.getTitle());
        assertEquals("New Description", bulletin.getDescription());
        assertEquals(999.99, bulletin.getPrice());
        assertEquals(leafCategory.getId(), bulletin.getCategory().getId());
    }

    @Test
    void shouldRemoveMissingCharacteristics() {
        // Arrange
        bulletin.setCategory(leafCategory);
        BulletinCharacteristic bc1 = bulletin.addCharacteristic(characteristic1);
        bc1.setValue(value1);
        BulletinCharacteristic bc2 = bulletin.addCharacteristic(characteristic2);
        bc2.setValue(value2);
        BulletinCharacteristic bc3 = bulletin.addCharacteristic(characteristic3);
        bc3.setValue(value3);

        List<BulletinCharacteristicRequest> characteristics = Arrays.asList(
                BulletinCharacteristicRequest.builder()
                        .characteristicId(characteristic1.getId())
                        .characteristicValueId(value1.getId())
                        .build(),
                BulletinCharacteristicRequest.builder()
                        .characteristicId(characteristic2.getId())
                        .characteristicValueId(value2.getId())
                        .build()
        );

        BulletinRequest request = BulletinRequest.builder()
                .title("Title")
                .description("Description")
                .price(100.0)
                .categoryId(leafCategory.getId())
                .characteristics(characteristics)
                .build();

        // Act
        modifyService.updateBulletin(bulletin, request);

        // Assert
        assertEquals(2, bulletin.getCharacteristics().size());
        assertTrue(bulletin.getCharacteristics().stream()
                .anyMatch(bc -> bc.getName().getId().equals(characteristic1.getId())));
        assertTrue(bulletin.getCharacteristics().stream()
                .anyMatch(bc -> bc.getName().getId().equals(characteristic2.getId())));
        assertFalse(bulletin.getCharacteristics().stream()
                .anyMatch(bc -> bc.getName().getId().equals(characteristic3.getId())));
    }

    @Test
    void shouldUpdateExistingCharacteristicValues() {
        // Arrange
        bulletin.setCategory(leafCategory);
        BulletinCharacteristic bc1 = bulletin.addCharacteristic(characteristic1);
        bc1.setValue(value1);

        when(characteristicValueRepository.findById(newValue1.getId()))
                .thenReturn(Optional.of(newValue1));

        List<BulletinCharacteristicRequest> characteristics = Collections.singletonList(
                BulletinCharacteristicRequest.builder()
                        .characteristicId(characteristic1.getId())
                        .characteristicValueId(newValue1.getId())
                        .build()
        );

        BulletinRequest request = BulletinRequest.builder()
                .title("Title")
                .description("Description")
                .price(100.0)
                .categoryId(leafCategory.getId())
                .characteristics(characteristics)
                .build();

        // Act
        modifyService.updateBulletin(bulletin, request);

        // Assert
        assertEquals(1, bulletin.getCharacteristics().size());
        BulletinCharacteristic updatedBc = bulletin.getCharacteristics().getFirst();
        assertEquals(newValue1.getId(), updatedBc.getValue().getId());
        assertEquals("Blue", updatedBc.getValue().getName());

        verify(characteristicValueRepository).findById(newValue1.getId());
    }

    @Test
    void shouldAddNewCharacteristics() {
        // Arrange
        bulletin.setCategory(leafCategory);
        when(characteristicRepository.findById(characteristic1.getId()))
                .thenReturn(Optional.of(characteristic1));
        when(characteristicRepository.findById(characteristic2.getId()))
                .thenReturn(Optional.of(characteristic2));
        when(characteristicValueRepository.findById(value1.getId()))
                .thenReturn(Optional.of(value1));
        when(characteristicValueRepository.findById(value2.getId()))
                .thenReturn(Optional.of(value2));

        List<BulletinCharacteristicRequest> characteristics = Arrays.asList(
                BulletinCharacteristicRequest.builder()
                        .characteristicId(characteristic1.getId())
                        .characteristicValueId(value1.getId())
                        .build(),
                BulletinCharacteristicRequest.builder()
                        .characteristicId(characteristic2.getId())
                        .characteristicValueId(value2.getId())
                        .build()
        );
        BulletinRequest request = BulletinRequest.builder()
                .title("Title")
                .description("Description")
                .price(100.0)
                .categoryId(leafCategory.getId())
                .characteristics(characteristics)
                .build();

        // Act
        modifyService.updateBulletin(bulletin, request);

        // Assert
        assertEquals(2, bulletin.getCharacteristics().size());

        Optional<BulletinCharacteristic> firstBc = bulletin.getCharacteristics().stream()
                .filter(bc -> bc.getName().getId().equals(characteristic1.getId()))
                .findFirst();
        assertTrue(firstBc.isPresent());
        assertEquals(value1.getId(), firstBc.get().getValue().getId());

        Optional<BulletinCharacteristic> secondBc = bulletin.getCharacteristics().stream()
                .filter(bc -> bc.getName().getId().equals(characteristic2.getId()))
                .findFirst();
        assertTrue(secondBc.isPresent());
        assertEquals(value2.getId(), secondBc.get().getValue().getId());

        verify(characteristicRepository).findById(characteristic1.getId());
        verify(characteristicRepository).findById(characteristic2.getId());
        verify(characteristicValueRepository).findById(value1.getId());
        verify(characteristicValueRepository).findById(value2.getId());
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {
        // Arrange
        UUID nonExistentCategoryId = UUID.randomUUID();
        BulletinRequest request = BulletinRequest.builder()
                .title("Title")
                .description("Description")
                .price(100.0)
                .categoryId(nonExistentCategoryId)
                .characteristics(new ArrayList<>())
                .build();

        when(categoryRepository.findById(nonExistentCategoryId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> modifyService.updateBulletin(bulletin, request)
        );
    }

    @Test
    void shouldThrowExceptionWhenCharacteristicNotFound() {
        // Arrange
        bulletin.setCategory(leafCategory);

        UUID nonExistentCharacteristicId = UUID.randomUUID();
        UUID valueId = UUID.randomUUID();

        List<BulletinCharacteristicRequest> characteristics = Collections.singletonList(
                BulletinCharacteristicRequest.builder()
                        .characteristicId(nonExistentCharacteristicId)
                        .characteristicValueId(valueId)
                        .build()
        );

        BulletinRequest request = BulletinRequest.builder()
                .title("Title")
                .description("Description")
                .price(100.0)
                .categoryId(leafCategory.getId())
                .characteristics(characteristics)
                .build();

        when(characteristicRepository.findById(nonExistentCharacteristicId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> modifyService.updateBulletin(bulletin, request));
    }

    @Test
    void shouldThrowExceptionWhenCharacteristicValueNotFound() {
        // Arrange
        bulletin.setCategory(leafCategory);

        UUID nonExistentValueId = UUID.randomUUID();

        List<BulletinCharacteristicRequest> characteristics = Collections.singletonList(
                BulletinCharacteristicRequest.builder()
                        .characteristicId(characteristic1.getId())
                        .characteristicValueId(nonExistentValueId)
                        .build()
        );

        BulletinRequest request = BulletinRequest.builder()
                .title("Title")
                .description("Description")
                .price(100.0)
                .categoryId(leafCategory.getId())
                .characteristics(characteristics)
                .build();

        when(characteristicRepository.findById(characteristic1.getId()))
                .thenReturn(Optional.of(characteristic1));
        when(characteristicValueRepository.findById(nonExistentValueId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> modifyService.updateBulletin(bulletin, request)
        );
    }

}
