package com.example.bulletin.unit.application.mapper;

import com.example.bulletin.application.data.response.BulletinCharacteristicResponse;
import com.example.bulletin.application.data.response.BulletinImageResponse;
import com.example.bulletin.application.data.response.BulletinResponse;
import com.example.bulletin.application.data.response.CategoryResponse;
import com.example.bulletin.application.mapper.*;
import com.example.bulletin.domain.entity.*;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import com.example.bulletin.domain.vo.*;
import com.example.bulletin.unit.application.mapper.helper.MapperBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class BulletinMapperToDataTests {

    private BulletinMapper bulletinMapper;
    private BulletinCharacteristicMapper bulletinCharacteristicMapper;


    private CategoryMapper categoryMapper = Mappers.getMapper(
            CategoryMapper.class);

    private BulletinImageMapper bulletinImageMapper = Mappers.getMapper(
            BulletinImageMapper.class);

    private Bulletin bulletin;
    private MapperBuilder mapperBuilder = new MapperBuilder();


    @BeforeEach
    void setUp() {
        bulletinMapper = mapperBuilder.createBulletinMapper();
        bulletinCharacteristicMapper = mapperBuilder.createBulletinCharacteristicMapper();
        bulletin = createBulletin();
    }

    @Test
    void shouldConvertCorrectlyFromEntityToData() {
        // Arrange
        BulletinData expected = createExpectedBulletinDataBuilder().build();

        // Act
        BulletinData actual = bulletinMapper.toData(bulletin);

        // Assert
        assertTrue(expected.equalsData(actual));
        assertThat(actual.getId()).isEqualTo(bulletin.getId());
    }

    @Test
    void shouldHandleEmptyCharacteristicsWhenConvertToData() {
        // Arrange
        bulletin.getCharacteristics().clear();
        BulletinData expected = createExpectedBulletinDataBuilder()
                .characteristics(List.of())
                .build();

        // Act
        BulletinData actual = bulletinMapper.toData(bulletin);

        // Assert
        assertTrue(expected.equalsData(actual));
    }

    @Test
    void shouldHandleEmptyImagesWhenConvertToData() {
        // Arrange
        bulletin.getImages().clear();
        BulletinData expected = createExpectedBulletinDataBuilder()
                .images(List.of())
                .build();

        // Act
        BulletinData actual = bulletinMapper.toData(bulletin);

        // Assert
        assertTrue(expected.equalsData(actual));
    }

    @Test
    void shouldConvertCorrectlyFromEntityToResponse() {
        // Arrange
        BulletinResponse expected = createExpectedBulletinResponseBuilder().build();

        // Act
        BulletinResponse actual = bulletinMapper.toResponse(bulletin);

        // Assert
        assertTrue(expected.equalsData(actual));
        assertTrue(bulletin.getId()
                .equals(actual.getId()));
    }

    @Test
    void shouldHandleEmptyCharacteristicsWhenConvertToResponse() {
        // Arrange
        bulletin.getCharacteristics().clear();
        BulletinResponse expected = createExpectedBulletinResponseBuilder()
                .characteristics(List.of())
                .build();

        // Act
        BulletinResponse actual = bulletinMapper.toResponse(bulletin);

        // Assert
        assertTrue(expected.equalsData(actual));
    }

    @Test
    void shouldHandleEmptyImageshenConvertToResponse() {
        // Arrange
        bulletin.getImages().clear();
        BulletinResponse expected = createExpectedBulletinResponseBuilder()
                .images(List.of())
                .build();

        // Act
        BulletinResponse actual = bulletinMapper.toResponse(bulletin);

        // Assert
        assertTrue(expected.equalsData(actual));
    }

    private Bulletin createBulletin() {
        User user = User.createUser(UUID.randomUUID(), "owner@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);

        Bulletin bulletin = Bulletin.createDraft(ownerInfo);
        bulletin.setTitle("Test Bulletin");
        bulletin.setDescription("Test Description");
        bulletin.setPrice(1000.0);
        bulletin.setRating(4.5);

        Category category = createCategory();
        bulletin.setCategory(category);

        BulletinCharacteristic characteristic = createBulletinCharacteristic(bulletin);
        bulletin.getCharacteristics().add(characteristic);

        BulletinImage image = createBulletinImage(bulletin);
        bulletin.getImages().add(image);

        return bulletin;
    }

    private BulletinData.BulletinDataBuilder createExpectedBulletinDataBuilder() {
        CategoryData categoryData = categoryMapper.toData(bulletin.getCategory());
        List<BulletinCharacteristicData> characteristicsData = bulletin.getCharacteristics().stream()
                .map(bulletinCharacteristicMapper::toData)
                .collect(Collectors.toList());
        List<BulletinImageData> imagesData =bulletin.getImages().stream()
                .map(bulletinImageMapper::toData)
                .toList();

        return BulletinData.builder()
                .id(bulletin.getId())
                .ownerId(bulletin.getOwnerInfo().getOwnerId())
                .title(bulletin.getTitle())
                .description(bulletin.getDescription())
                .price(bulletin.getPrice())
                .rating(bulletin.getRating())
                .state(BulletinState.CREATED)
                .category(categoryData)
                .characteristics(characteristicsData)
                .images(imagesData);
    }

    private BulletinResponse.BulletinResponseBuilder createExpectedBulletinResponseBuilder() {
        CategoryResponse categoryResponse = categoryMapper.toResponse(bulletin.getCategory());
        List<BulletinCharacteristicResponse> characteristicsResponse = bulletin.getCharacteristics().stream()
                .map(bulletinCharacteristicMapper::toResponse)
                .collect(Collectors.toList());
        List<BulletinImageResponse> imagesResponse = bulletin.getImages().stream()
                .map(bulletinImageMapper::toResponse)
                .toList();

        return BulletinResponse.builder()
                .id(bulletin.getId())
                .ownerId(bulletin.getOwnerInfo().getOwnerId())
                .title(bulletin.getTitle())
                .description(bulletin.getDescription())
                .price(bulletin.getPrice())
                .rating(bulletin.getRating())
                .state(BulletinState.CREATED)
                .category(categoryResponse)
                .characteristics(characteristicsResponse)
                .images(imagesResponse);
    }

    private Category createCategory() {
        Category root = Category.createRoot("Root Category");
        Category child = root.createLeafyChild("Child Category");
        return child;
    }

    private BulletinCharacteristic createBulletinCharacteristic(Bulletin bulletin) {
        Category category = bulletin.getCategory();
        Characteristic characteristic = category.addCharacteristic("Color");
        CharacteristicValue value = characteristic.addPossibleValue("Red");

        BulletinCharacteristic bulletinCharacteristic = BulletinCharacteristic.createBulletinCharacteristic(bulletin, characteristic);
        bulletinCharacteristic.setValue(value);
        return bulletinCharacteristic;
    }

    private BulletinImage createBulletinImage(Bulletin bulletin) {
        return BulletinImage.createBulletinImage(bulletin, UUID.randomUUID());
    }

}