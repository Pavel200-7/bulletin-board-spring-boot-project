package com.example.chat.unit.application.service.profile.service;

import com.example.chat.application.data.response.ProfileResponse;
import com.example.chat.application.mapper.ProfileMapper;
import com.example.chat.application.service.profile.ProfileServiceImpl;
import com.example.chat.application.service.profile.data.request.GetExistsMyProfileRequest;
import com.example.chat.application.service.profile.data.response.GetExistsMyProfileResponse;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GetExistsMyProfileTests {

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

        when(mapper.toResponse(any(Profile.class)))
                .thenAnswer(invocation -> {
                    Profile profile = invocation.getArgument(0);
                    return mapperHelper.toResponse(profile);
                });
    }

    @Test
    public void shouldReturnExistsTrueAndProfileWhenProfileExists() {
        // Arrange
        GetExistsMyProfileRequest request = new GetExistsMyProfileRequest();
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.of(profile));

        ProfileResponse expectedResponse = mapperHelper.toResponse(profile);

        // Act
        GetExistsMyProfileResponse response = service.existsMyProfile(request);

        // Assert
        assertThat(response.isExists()).isTrue();
        assertThat(response.getProfileResponse())
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(profileRepository).findByOwnerInfoOwnerId(currentUserId);
    }

    @Test
    public void shouldReturnExistsFalseAndNullProfileWhenProfileDoesNotExist() {
        // Arrange
        GetExistsMyProfileRequest request = new GetExistsMyProfileRequest();
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.empty());

        // Act
        GetExistsMyProfileResponse response = service.existsMyProfile(request);

        // Assert
        assertThat(response.isExists()).isFalse();
        assertThat(response.getProfileResponse()).isNull();

        verify(profileRepository).findByOwnerInfoOwnerId(currentUserId);
    }

    @Test
    public void shouldUseCurrentUserIdFromSecurityService() {
        // Arrange
        GetExistsMyProfileRequest request = new GetExistsMyProfileRequest();
        UUID expectedUserId = UUID.randomUUID();
        when(securityService.getCurrentUserIdAsUUID()).thenReturn(expectedUserId);

        Profile profileForExpectedUser = createProfileWithUserId(expectedUserId);
        when(profileRepository.findByOwnerInfoOwnerId(expectedUserId))
                .thenReturn(Optional.of(profileForExpectedUser));

        // Act
        service.existsMyProfile(request);

        // Assert
        verify(securityService).getCurrentUserIdAsUUID();
        verify(profileRepository).findByOwnerInfoOwnerId(expectedUserId);
    }

    @Test
    public void shouldReturnCorrectProfileDataWhenExists() {
        // Arrange
        GetExistsMyProfileRequest request = new GetExistsMyProfileRequest();
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.of(profile));

        // Act
        GetExistsMyProfileResponse response = service.existsMyProfile(request);
        ProfileResponse actual = response.getProfileResponse();

        // Assert
        assertThat(actual.getId()).isEqualTo(profile.getId());
        assertThat(actual.getOwnerId()).isEqualTo(profile.getOwnerId());
        assertThat(actual.getPublicName()).isEqualTo(profile.getPublicName());
        assertThat(actual.getDescription()).isEqualTo(profile.getDescription());
        assertThat(actual.getImageId()).isEqualTo(profile.getImageId());
    }

    @Test
    public void shouldLogInformationWhenProfileExists() {
        // Arrange
        GetExistsMyProfileRequest request = new GetExistsMyProfileRequest();
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.of(profile));

        // Act
        service.existsMyProfile(request);

        // Assert - проверяем, что методы были вызваны
        verify(securityService).getCurrentUserIdAsUUID();
        verify(profileRepository).findByOwnerInfoOwnerId(currentUserId);
    }

    @Test
    public void shouldLogInformationWhenProfileDoesNotExist() {
        // Arrange
        GetExistsMyProfileRequest request = new GetExistsMyProfileRequest();
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.empty());

        // Act
        service.existsMyProfile(request);

        // Assert
        verify(securityService).getCurrentUserIdAsUUID();
        verify(profileRepository).findByOwnerInfoOwnerId(currentUserId);
    }

    @Test
    public void shouldHandleProfileWithNullFields() {
        // Arrange
        GetExistsMyProfileRequest request = new GetExistsMyProfileRequest();

        // Создаем профиль с null полями
        User user = User.createUser(currentUserId, "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        Profile profileWithNulls = Profile.createProfile(ownerInfo, "Test User");
        profileWithNulls.changeDescription(null);
        profileWithNulls.changeImage(null);

        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.of(profileWithNulls));

        ProfileResponse expected = mapperHelper.toResponse(profileWithNulls);

        // Act
        GetExistsMyProfileResponse response = service.existsMyProfile(request);
        ProfileResponse actual = response.getProfileResponse();

        // Assert
        assertThat(response.isExists()).isTrue();
        assertThat(actual.getDescription()).isNull();
        assertThat(actual.getImageId()).isNull();
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    public void shouldMapAllFieldsCorrectlyWhenProfileExists() {
        // Arrange
        GetExistsMyProfileRequest request = new GetExistsMyProfileRequest();

        // Создаем профиль со всеми заполненными полями
        User user = User.createUser(currentUserId, "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        Profile fullProfile = Profile.createProfile(ownerInfo, "Full Name");
        fullProfile.changeDescription("This is a complete profile description");
        fullProfile.changeImage(UUID.randomUUID());

        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.of(fullProfile));

        ProfileResponse expected = mapperHelper.toResponse(fullProfile);

        // Act
        GetExistsMyProfileResponse response = service.existsMyProfile(request);
        ProfileResponse actual = response.getProfileResponse();

        // Assert
        assertThat(response.isExists()).isTrue();
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);

        assertThat(actual.getPublicName()).isEqualTo("Full Name");
        assertThat(actual.getDescription()).isEqualTo("This is a complete profile description");
        assertThat(actual.getImageId()).isNotNull();
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