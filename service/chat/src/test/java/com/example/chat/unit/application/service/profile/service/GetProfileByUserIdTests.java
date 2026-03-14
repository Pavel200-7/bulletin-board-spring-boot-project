package com.example.chat.unit.application.service.profile.service;

import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.mapper.ProfileMapper;
import com.example.chat.application.service.profile.ProfileServiceImpl;
import com.example.chat.application.service.profile.data.request.GetProfileByUserIdRequest;
import com.example.chat.application.service.profile.validator.ProfileAccessValidator;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.entity.base.user.User;
import com.example.chat.infrastructure.repository.ProfileRepository;
import com.example.chat.infrastructure.security.SecurityService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GetProfileByUserIdTests {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private ProfileMapper profileMapper;

    @Mock
    private ProfileAccessValidator accessValidator;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private ProfileMapper mapperHelper = Mappers.getMapper(ProfileMapper.class);

    private UUID targetUserId;
    private UUID currentUserId;
    private Profile profile;

    @BeforeEach
    void setUp() {
        targetUserId = UUID.randomUUID();
        currentUserId = UUID.randomUUID();

        profile = createProfile(targetUserId);

        when(securityService.getCurrentUserIdAsUUID()).thenReturn(currentUserId);

        when(profileMapper.toResponse(any(Profile.class)))
                .thenAnswer(invocation -> {
                    Profile profile = invocation.getArgument(0);
                    return mapperHelper.toResponse(profile);
                });

        when(profileRepository.findByOwnerInfoOwnerId(targetUserId))
                .thenReturn(Optional.of(profile));
    }

    @Test
    void shouldGetProfileSuccessfully() {
        // Arrange
        GetProfileByUserIdRequest request = GetProfileByUserIdRequest.builder()
                .id(targetUserId)
                .build();

        // Act
        var response = profileService.getProfileByUserId(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getProfileResponse());
        assertEquals(profile.getId(), response.getProfileResponse().getId());
        assertEquals(targetUserId, response.getProfileResponse().getOwnerId());
        assertEquals(profile.getPublicName(), response.getProfileResponse().getPublicName());
        assertEquals(profile.getDescription(), response.getProfileResponse().getDescription());
        assertEquals(profile.getImageId(), response.getProfileResponse().getImageId());

        verify(profileRepository).findByOwnerInfoOwnerId(targetUserId);
        verify(profileMapper).toResponse(profile);
    }

    @Test
    void shouldThrowWhenProfileNotFound() {
        // Arrange
        GetProfileByUserIdRequest request = GetProfileByUserIdRequest.builder()
                .id(targetUserId)
                .build();

        when(profileRepository.findByOwnerInfoOwnerId(targetUserId))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> profileService.getProfileByUserId(request));
    }

    private Profile createProfile(UUID userId) {
        User user = User.createUser(userId, "user" + userId + "@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        String profileName = "User " + userId.toString().substring(0, 8);
        return Profile.createProfile(ownerInfo, profileName);
    }

}