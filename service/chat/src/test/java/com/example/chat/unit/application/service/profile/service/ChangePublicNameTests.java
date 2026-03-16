package com.example.chat.unit.application.service.profile.service;

import com.example.chat.application.exception.AccessDeniedException;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.mapper.ProfileMapper;
import com.example.chat.application.service.profile.ProfileServiceImpl;
import com.example.chat.application.service.profile.data.request.ChangePublicNameRequest;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ChangePublicNameTests {

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

    @Captor
    private ArgumentCaptor<Profile> profileCaptor;

    private ProfileMapper mapperHelper = Mappers.getMapper(ProfileMapper.class);

    private UUID currentUserId;
    private Profile profile;
    private String oldPublicName;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();

        profile = createProfile();
        oldPublicName = profile.getPublicName();

        when(securityService.getCurrentUserIdAsUUID()).thenReturn(currentUserId);
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.of(profile));

        when(profileMapper.toResponse(any(Profile.class)))
                .thenAnswer(invocation -> {
                    Profile profile = invocation.getArgument(0);
                    return mapperHelper.toResponse(profile);
                });

        doNothing().when(accessValidator).validateOwnership(profile);
    }

    @Test
    void shouldChangePublicNameSuccessfully() {
        // Arrange
        String newName = "New Public Name";
        ChangePublicNameRequest request = ChangePublicNameRequest.builder()
                .name(newName)
                .build();

        // Act
        var response = profileService.changePublicName(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getProfileResponse());
        assertEquals(newName, response.getProfileResponse().getPublicName());

        verify(profileRepository).save(profileCaptor.capture());
        Profile updatedProfile = profileCaptor.getValue();

        assertEquals(newName, updatedProfile.getPublicName());
    }

    @Test
    void shouldTrimWhitespaceFromNewName() {
        // Arrange
        String newNameWithSpaces = "  New Public Name  ";
        String expectedName = "New Public Name";

        ChangePublicNameRequest request = ChangePublicNameRequest.builder()
                .name(newNameWithSpaces)
                .build();

        // Act
        profileService.changePublicName(request);

        // Assert
        verify(profileRepository).save(profileCaptor.capture());
        Profile updatedProfile = profileCaptor.getValue();

        assertEquals(expectedName, updatedProfile.getPublicName());
    }

    @Test
    void shouldThrowWhenProfileNotFound() {
        // Arrange
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.empty());

        ChangePublicNameRequest request = ChangePublicNameRequest.builder()
                .name("New Name")
                .build();

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> profileService.changePublicName(request));
    }

    @Test
    void shouldThrowWhenUserDoesNotOwnProfile() {
        // Arrange
        doThrow(new AccessDeniedException("You don't have permission to modify this profile"))
                .when(accessValidator).validateOwnership(profile);

        ChangePublicNameRequest request = ChangePublicNameRequest.builder()
                .name("New Name")
                .build();

        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> profileService.changePublicName(request));
    }

    @Test
    void shouldNotChangeNameWhenValidationFails() {
        // Arrange
        String newName = "New Name";
        ChangePublicNameRequest request = ChangePublicNameRequest.builder()
                .name(newName)
                .build();

        doThrow(new AccessDeniedException("Access denied"))
                .when(accessValidator).validateOwnership(profile);

        // Act
        assertThrows(AccessDeniedException.class,
                () -> profileService.changePublicName(request));

        // Assert
        assertEquals(oldPublicName, profile.getPublicName());
    }

    @Test
    void shouldAllowSameName() {
        // Arrange
        ChangePublicNameRequest request = ChangePublicNameRequest.builder()
                .name(oldPublicName)
                .build();

        // Act
        profileService.changePublicName(request);

        // Assert
        verify(profileRepository).save(profileCaptor.capture());
        Profile updatedProfile = profileCaptor.getValue();

        assertEquals(oldPublicName, updatedProfile.getPublicName());
    }

    private Profile createProfile() {
        User user = User.createUser(currentUserId, "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        String profileName = "Some Name";
        return Profile.createProfile(ownerInfo, profileName);
    }

}