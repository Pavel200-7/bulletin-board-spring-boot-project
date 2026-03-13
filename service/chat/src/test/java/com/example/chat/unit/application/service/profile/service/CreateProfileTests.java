package com.example.chat.unit.application.service.profile.service;


import com.example.chat.application.data.response.ProfileResponse;
import com.example.chat.application.exception.AccessDeniedException;
import com.example.chat.application.exception.DuplicateResourceException;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.mapper.ProfileMapper;
import com.example.chat.application.service.profile.ProfileServiceImpl;
import com.example.chat.application.service.profile.data.request.CreateProfileRequest;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.entity.base.user.User;
import com.example.chat.infrastructure.repository.ProfileRepository;
import com.example.chat.infrastructure.repository.UserRepository;
import com.example.chat.infrastructure.security.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CreateProfileTests {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private ProfileMapper profileMapper;

    @InjectMocks
    private ProfileServiceImpl profileService;

    @Captor
    private ArgumentCaptor<Profile> profileCaptor;

    private ProfileMapper mapperHelper = Mappers.getMapper(ProfileMapper.class);

    private UUID currentUserId;
    private User currentUser;
    private Profile expectedProfile;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        currentUser = User.createUser(currentUserId, "test@example.com");

        OwnerInfo ownerInfo = new OwnerInfo(currentUser);
        expectedProfile = Profile.createProfile(ownerInfo, "Test User");

        when(securityService.getCurrentUserIdAsUUID()).thenReturn(currentUserId);

        when(profileMapper.toResponse(any(Profile.class)))
                .thenAnswer(invocation -> {
                    Profile profile = invocation.getArgument(0);
                    return mapperHelper.toResponse(profile);
                });
    }

    @Test
    void shouldCreateProfileSuccessfully() {
        // Arrange
        CreateProfileRequest request = CreateProfileRequest.builder()
                .ownerId(currentUserId)
                .ownerName("Test User")
                .build();

        when(profileRepository.existsByOwnerInfoOwnerId(currentUserId)).thenReturn(false);
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));
        when(profileRepository.save(any(Profile.class))).thenReturn(expectedProfile);

        ProfileResponse expectedResponse = ProfileResponse.builder()
                .id(expectedProfile.getId())
                .ownerId(currentUserId)
                .publicName("Test User")
                .description(null)
                .imageId(null)
                .build();

        when(profileMapper.toResponse(any(Profile.class))).thenReturn(expectedResponse);

        // Act
        var response = profileService.createProfile(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getProfileResponse());
        assertEquals(expectedResponse, response.getProfileResponse());

        verify(profileRepository).save(profileCaptor.capture());
        Profile savedProfile = profileCaptor.getValue();

        assertThat(savedProfile)
                .usingRecursiveComparison()
                .ignoringFields("id", "contacts", "chatParticipants")
                .isEqualTo(expectedProfile);
    }

    @Test
    void shouldCreateProfileWithDefaultNameWhenOwnerNameIsNull() {
        // Arrange
        CreateProfileRequest request = CreateProfileRequest.builder()
                .ownerId(currentUserId)
                .ownerName(null)
                .build();

        when(profileRepository.existsByOwnerInfoOwnerId(currentUserId)).thenReturn(false);
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));

        Profile profileWithDefaultName = Profile.createProfile(new OwnerInfo(currentUser), currentUser.getEmail());
        when(profileRepository.save(any(Profile.class))).thenReturn(profileWithDefaultName);

        // Act
        profileService.createProfile(request);

        // Assert
        verify(profileRepository).save(profileCaptor.capture());
        Profile savedProfile = profileCaptor.getValue();

        assertEquals(currentUser.getEmail(), savedProfile.getPublicName());
    }

    @Test
    void shouldThrowWhenProfileAlreadyExists() {
        // Arrange
        CreateProfileRequest request = CreateProfileRequest.builder()
                .ownerId(currentUserId)
                .ownerName("Test User")
                .build();

        when(profileRepository.existsByOwnerInfoOwnerId(currentUserId)).thenReturn(true);

        // Act & Assert
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> profileService.createProfile(request));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        // Arrange
        CreateProfileRequest request = CreateProfileRequest.builder()
                .ownerId(currentUserId)
                .ownerName("Test User")
                .build();

        when(profileRepository.existsByOwnerInfoOwnerId(currentUserId)).thenReturn(false);
        when(userRepository.findById(currentUserId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> profileService.createProfile(request));
    }

    @Test
    void shouldReturnCorrectlyMappedResponse() {
        // Arrange
        CreateProfileRequest request = CreateProfileRequest.builder()
                .ownerId(currentUserId)
                .ownerName("Test User")
                .build();

        when(profileRepository.existsByOwnerInfoOwnerId(currentUserId)).thenReturn(false);
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));

        Profile savedProfile = Profile.createProfile(new OwnerInfo(currentUser), "Test User");
        when(profileRepository.save(any(Profile.class))).thenReturn(savedProfile);

        ProfileResponse expectedResponse = ProfileResponse.builder()
                .id(savedProfile.getId())
                .ownerId(currentUserId)
                .publicName("Test User")
                .description(null)
                .imageId(null)
                .build();

        when(profileMapper.toResponse(savedProfile)).thenReturn(expectedResponse);

        // Act
        var response = profileService.createProfile(request);

        // Assert
        assertNotNull(response.getProfileResponse());
        assertEquals(expectedResponse.getOwnerId(), response.getProfileResponse().getOwnerId());
        assertEquals(expectedResponse.getPublicName(), response.getProfileResponse().getPublicName());
        assertNull(response.getProfileResponse().getDescription());
        assertNull(response.getProfileResponse().getImageId());
    }
}