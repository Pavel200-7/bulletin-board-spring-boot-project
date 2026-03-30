package com.example.chat.unit.application.mapper;

import com.example.chat.application.data.response.ProfileResponse;
import com.example.chat.application.mapper.ProfileMapper;
import com.example.chat.application.service.profile.data.response.data.ProfilePaginationData;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.entity.base.user.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class ProfileMapperTests {

    private final ProfileMapper mapper = Mappers.getMapper(ProfileMapper.class);

    @Test
    void shouldMapProfileToProfileResponse() {
        // Arrange
        Profile profile = createProfile();

        // Act
        ProfileResponse response = mapper.toResponse(profile);

        // Assert
        assertNotNull(response);
        assertEquals(profile.getId(), response.getId());
        assertEquals(profile.getOwnerId(), response.getOwnerId());
        assertEquals(profile.getPublicName(), response.getPublicName());
        assertEquals(profile.getDescription(), response.getDescription());
        assertEquals(profile.getImageId(), response.getImageId());
    }

    @Test
    void shouldMapProfileToPaginationData() {
        // Arrange
        Profile profile = createProfile();

        // Act
        ProfilePaginationData response = mapper.toPaginationData(profile, true);

        // Assert
        assertNotNull(response);
        assertEquals(profile.getId(), response.getId());
        assertEquals(profile.getOwnerId(), response.getOwnerId());
        assertEquals(profile.getPublicName(), response.getPublicName());
        assertEquals(profile.getDescription(), response.getDescription());
        assertEquals(profile.getImageId(), response.getImageId());
        assertTrue(response.isContact());
    }

    @Test
    void shouldVerifyThatResponseAndPaginationDataAreConsistent() {
        // Arrange
        Profile profile = createProfile();

        // Act
        ProfileResponse response = mapper.toResponse(profile);
        ProfilePaginationData paginationData = mapper.toPaginationData(profile, true);

        // Assert
        assertEquals(response.getId(), paginationData.getId());
        assertEquals(response.getOwnerId(), paginationData.getOwnerId());
        assertEquals(response.getPublicName(), paginationData.getPublicName());
        assertEquals(response.getDescription(), paginationData.getDescription());
        assertEquals(response.getImageId(), paginationData.getImageId());
        assertTrue(paginationData.isContact());

    }

    private Profile createProfile() {
        UUID userId = UUID.randomUUID();
        User user = User.createUser(userId, "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        String randomName = userId + " Test Public Name";
        Profile profile = Profile.createProfile(ownerInfo, randomName);
        profile.changeDescription("Test Description");
        profile.changeImage(UUID.randomUUID());
        return profile;
    }

}