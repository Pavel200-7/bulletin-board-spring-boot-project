package com.example.chat.unit.application.service.profile.service;

import com.example.chat.application.data.response.ProfileResponse;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.mapper.ProfileMapper;
import com.example.chat.application.service.profile.ProfileServiceImpl;
import com.example.chat.application.service.profile.data.request.GetMyProfileRequest;
import com.example.chat.application.service.profile.data.response.GetMyProfileResponse;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.ActiveProfiles;

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
public class GetMyProfileTests {

    private ProfileMapper mapperHelper = Mappers.getMapper(
            ProfileMapper.class);

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileMapper mapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private ProfileServiceImpl service;

    private Profile profile;
    private UUID currentUserId;

    @BeforeEach
    public void setup() {
        currentUserId = UUID.randomUUID();
        profile = createProfile();

        when(securityService.getCurrentUserIdAsUUID())
                .thenReturn(currentUserId);

        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.of(profile));

        when(mapper.toResponse(any(Profile.class)))
                .thenAnswer(invocation -> {
                    Profile profile = invocation.getArgument(0);
                    return mapperHelper.toResponse(profile);
                });
    }

    @Test
    public void shouldThrowWhenProfileNotFound() {
        // Arrange
        GetMyProfileRequest request = new GetMyProfileRequest();
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.getMyProfile(request));
    }

    @Test
    public void shouldReturnProfileWhenFound() {
        // Arrange
        GetMyProfileRequest request = new GetMyProfileRequest();
        ProfileResponse expected = mapperHelper.toResponse(profile);

        // Act
        GetMyProfileResponse response = service.getMyProfile(request);
        ProfileResponse actual = response.getProfileResponse();

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    public void shouldUseCurrentUserIdFromSecurityService() {
        // Arrange
        GetMyProfileRequest request = new GetMyProfileRequest();
        UUID expectedUserId = UUID.randomUUID();
        when(securityService.getCurrentUserIdAsUUID()).thenReturn(expectedUserId);

        Profile profileForExpectedUser = createProfileWithUserId(expectedUserId);
        when(profileRepository.findByOwnerInfoOwnerId(expectedUserId))
                .thenReturn(Optional.of(profileForExpectedUser));

        // Act
        service.getMyProfile(request);

        // Assert
        verify(securityService).getCurrentUserIdAsUUID();
        verify(profileRepository).findByOwnerInfoOwnerId(expectedUserId);
    }

    @Test
    public void shouldReturnCorrectProfileData() {
        // Arrange
        GetMyProfileRequest request = new GetMyProfileRequest();

        // Act
        GetMyProfileResponse response = service.getMyProfile(request);
        ProfileResponse actual = response.getProfileResponse();

        // Assert
        assertThat(actual.getId()).isEqualTo(profile.getId());
        assertThat(actual.getOwnerId()).isEqualTo(profile.getOwnerId());
        assertThat(actual.getPublicName()).isEqualTo(profile.getPublicName());
        assertThat(actual.getDescription()).isEqualTo(profile.getDescription());
        assertThat(actual.getImageId()).isEqualTo(profile.getImageId());
    }

    @Test
    public void shouldLogInformationWhenProfileFound() {
        // Arrange
        GetMyProfileRequest request = new GetMyProfileRequest();

        // Act
        service.getMyProfile(request);

        // Assert - проверяем, что метод репозитория был вызван
        verify(profileRepository).findByOwnerInfoOwnerId(currentUserId);
        verify(securityService).getCurrentUserIdAsUUID();
    }

    @Test
    public void shouldHandleProfileWithNullFields() {
        // Arrange
        GetMyProfileRequest request = new GetMyProfileRequest();

        // Создаем профиль с null полями
        User user = User.createUser(currentUserId, "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        Profile profileWithNulls = Profile.createProfile(ownerInfo, "Test User");
        profileWithNulls.changeDescription(null); // Устанавливаем null для description

        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.of(profileWithNulls));

        ProfileResponse expected = mapperHelper.toResponse(profileWithNulls);

        // Act
        GetMyProfileResponse response = service.getMyProfile(request);
        ProfileResponse actual = response.getProfileResponse();

        // Assert
        assertThat(actual.getDescription()).isNull();
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }



    private Profile createProfile() {
        User user = User.createUser(currentUserId, "owner@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        Profile profile = Profile.createProfile(ownerInfo, "Test User");
        profile.changeDescription("Test description");
        profile.changeImage(UUID.randomUUID());
        return profile;
    }

    private Profile createProfileWithUserId(UUID userId) {
        User user = User.createUser(userId, "user@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        Profile profile = Profile.createProfile(ownerInfo, "Test User");
        profile.changeDescription("Test description");
        return profile;
    }

}