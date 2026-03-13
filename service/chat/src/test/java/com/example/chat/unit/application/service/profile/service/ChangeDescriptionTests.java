package com.example.chat.unit.application.service.profile.service;

import com.example.chat.application.data.response.ProfileResponse;
import com.example.chat.application.exception.AccessDeniedException;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.mapper.ProfileMapper;
import com.example.chat.application.service.profile.ProfileServiceImpl;
import com.example.chat.application.service.profile.data.request.ChangeDescriptionRequest;
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
public class ChangeDescriptionTests {

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
    private String oldDescription;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();

        profile = createProfile();
        oldDescription = profile.getDescription();

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
    void shouldChangeDescriptionSuccessfully() {
        // Arrange
        String newDescription = "This is a new description for the profile";
        ChangeDescriptionRequest request = ChangeDescriptionRequest.builder()
                .description(newDescription)
                .build();

        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        // Act
        var response = profileService.changeDescription(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getProfileResponse());
        assertEquals(newDescription, response.getProfileResponse().getDescription());

        verify(profileRepository).save(profileCaptor.capture());
        Profile updatedProfile = profileCaptor.getValue();

        assertEquals(newDescription, updatedProfile.getDescription());
    }

    @Test
    void shouldThrowWhenProfileNotFound() {
        // Arrange
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.empty());

        ChangeDescriptionRequest request = ChangeDescriptionRequest.builder()
                .description("New description")
                .build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> profileService.changeDescription(request));
    }

    @Test
    void shouldThrowWhenUserDoesNotOwnProfile() {
        // Arrange
        doThrow(new AccessDeniedException("You don't have permission to modify this profile"))
                .when(accessValidator).validateOwnership(profile);

        ChangeDescriptionRequest request = ChangeDescriptionRequest.builder()
                .description("New description")
                .build();

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> profileService.changeDescription(request));
    }

    @Test
    void shouldNotChangeDescriptionWhenValidationFails() {
        // Arrange
        String newDescription = "New description";
        ChangeDescriptionRequest request = ChangeDescriptionRequest.builder()
                .description(newDescription)
                .build();

        doThrow(new AccessDeniedException("Access denied"))
                .when(accessValidator).validateOwnership(profile);

        // Assert & Act
        assertThrows(AccessDeniedException.class,
                () -> profileService.changeDescription(request));
    }

    private Profile createProfile() {
        User user = User.createUser(currentUserId, "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        String profileName = "Some Name";
        return Profile.createProfile(ownerInfo, profileName);
    }

}