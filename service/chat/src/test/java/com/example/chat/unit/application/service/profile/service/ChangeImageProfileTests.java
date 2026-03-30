package com.example.chat.unit.application.service.profile.service;

import com.example.chat.application.data.response.ProfileResponse;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.mapper.ProfileMapper;
import com.example.chat.application.service.profile.ProfileServiceImpl;
import com.example.chat.application.service.profile.data.request.ChangeImageProfileRequest;
import com.example.chat.application.service.profile.data.response.ChangeImageProfileResponse;
import com.example.chat.application.service.profile.validator.ProfileAccessValidator;
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
import org.mockito.*;
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
public class ChangeImageProfileTests {

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

    @Mock
    private ProfileAccessValidator accessValidator;

    @InjectMocks
    private ProfileServiceImpl service;

    @Captor
    private ArgumentCaptor<Profile> profileCaptor;

    private Profile profile;
    private UUID currentUserId;
    private UUID imageId;

    @BeforeEach
    public void setup() {
        currentUserId = UUID.randomUUID();
        imageId = UUID.randomUUID();

        profile = createProfile();

        when(securityService.getCurrentUserIdAsUUID())
                .thenReturn(currentUserId);

        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.of(profile));

        when(profileRepository.save(any(Profile.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(mapper.toResponse(any(Profile.class)))
                .thenAnswer(invocation -> {
                    Profile account = invocation.getArgument(0);
                    return mapperHelper.toResponse(account);
                });
    }

    @Test
    public void shouldThrowWhenProfileNotFound() {
        // Arrange
        ChangeImageProfileRequest request = createRequest();
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.changeImage(request));
    }

    @Test
    public void shouldChangeImageIdAndSave() {
        // Arrange
        ChangeImageProfileRequest request = createRequest();

        // Act
        service.changeImage(request);

        // Assert
        verify(profileRepository).save(profileCaptor.capture());
        Profile actual = profileCaptor.getValue();

        assertThat(actual.getImageId()).isEqualTo(request.getImageId());
    }

    @Test
    public void shouldValidateOwnership() {
        // Arrange
        ChangeImageProfileRequest request = createRequest();

        // Act
        service.changeImage(request);

        // Assert
        verify(accessValidator).validateOwnership(profile);
    }

    @Test
    public void shouldMapBeforeReturn() {
        // Arrange
        ChangeImageProfileRequest request = createRequest();
        ProfileResponse expected = mapperHelper.toResponse(profile);
        expected = ProfileResponse.builder()
                .id(expected.getId())
                .ownerId(expected.getOwnerId())
                .publicName(expected.getPublicName())
                .description(expected.getDescription())
                .imageId(request.getImageId())
                .build();

        // Act
        ChangeImageProfileResponse response = service.changeImage(request);
        ProfileResponse actual = response.getProfileResponse();

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    public void shouldUpdateImageIdCorrectly() {
        // Arrange
        UUID newImageId = UUID.randomUUID();
        ChangeImageProfileRequest request = ChangeImageProfileRequest.builder()
                .imageId(newImageId)
                .build();

        // Act
        service.changeImage(request);

        // Assert
        verify(profileRepository).save(profileCaptor.capture());
        Profile savedProfile = profileCaptor.getValue();
        assertThat(savedProfile.getImageId()).isEqualTo(newImageId);
    }



    private ChangeImageProfileRequest createRequest() {
        return ChangeImageProfileRequest.builder()
                .imageId(UUID.randomUUID())
                .build();
    }

    private Profile createProfile() {
        User user = User.createUser(currentUserId, "owner@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        Profile profile = Profile.createProfile(ownerInfo, "Test User");
        profile.changeDescription("Test description");
        return profile;
    }

}