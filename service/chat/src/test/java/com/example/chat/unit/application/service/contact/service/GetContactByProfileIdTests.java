package com.example.chat.unit.application.service.contact.service;

import com.example.chat.application.data.response.ContactResponse;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.mapper.ContactMapper;
import com.example.chat.application.service.contact.ContactServiceImpl;
import com.example.chat.application.service.contact.data.request.GetContactByProfileIdRequest;
import com.example.chat.application.service.contact.data.response.GetContactByProfileIdResponse;
import com.example.chat.domain.entity.Contact;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.entity.base.user.User;
import com.example.chat.infrastructure.repository.ContactRepository;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GetContactByProfileIdTests {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private ContactMapper contactMapper;

    @InjectMocks
    private ContactServiceImpl contactService;

    private ContactMapper mapperHelper = Mappers.getMapper(ContactMapper.class);

    private UUID currentUserId;
    private UUID contactProfileId;
    private Profile currentProfile;
    private Profile contactProfile;
    private Contact contact;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        contactProfileId = UUID.randomUUID();

        currentProfile = createProfile(currentUserId);
        contactProfile = createProfile(contactProfileId);
        contact = createContact(currentProfile, contactProfile);

        when(securityService.getCurrentUserIdAsUUID())
                .thenReturn(currentUserId);
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.of(currentProfile));
        when(profileRepository.findById(contactProfileId))
                .thenReturn(Optional.of(contactProfile));
        when(contactRepository.findByProfilesId(currentProfile.getId(), contactProfile.getId()))
                .thenReturn(Optional.of(contact));
        when(contactMapper.toResponse(any(Contact.class)))
                .thenAnswer(invocation -> {
                    Contact c = invocation.getArgument(0);
                    return mapperHelper.toResponse(c);
                });
    }

    @Test
    void shouldGetContactByProfileIdSuccessfully() {
        // Arrange
        GetContactByProfileIdRequest request = GetContactByProfileIdRequest.builder()
                .profileId(contactProfileId)
                .build();

        ContactResponse expectedResponse = mapperHelper.toResponse(contact);

        // Act
        GetContactByProfileIdResponse response = contactService.getContactByProfileId(request);
        ContactResponse actual = response.getContactResponse();

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(contactRepository).findByProfilesId(currentProfile.getId(), contactProfile.getId());
    }

    @Test
    void shouldThrowWhenCurrentProfileNotFound() {
        // Arrange
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.empty());

        GetContactByProfileIdRequest request = GetContactByProfileIdRequest.builder()
                .profileId(contactProfileId)
                .build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> contactService.getContactByProfileId(request));
    }

    @Test
    void shouldThrowWhenContactProfileNotFound() {
        // Arrange
        when(profileRepository.findById(contactProfileId))
                .thenReturn(Optional.empty());

        GetContactByProfileIdRequest request = GetContactByProfileIdRequest.builder()
                .profileId(contactProfileId)
                .build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> contactService.getContactByProfileId(request));
    }

    @Test
    void shouldThrowWhenContactNotFound() {
        // Arrange
        when(contactRepository.findByProfilesId(currentProfile.getId(), contactProfile.getId()))
                .thenReturn(Optional.empty());

        GetContactByProfileIdRequest request = GetContactByProfileIdRequest.builder()
                .profileId(contactProfileId)
                .build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> contactService.getContactByProfileId(request));
    }

    @Test
    void shouldUseCorrectParametersForRepository() {
        // Arrange
        GetContactByProfileIdRequest request = GetContactByProfileIdRequest.builder()
                .profileId(contactProfileId)
                .build();

        // Act
        contactService.getContactByProfileId(request);

        // Assert
        verify(contactRepository).findByProfilesId(currentProfile.getId(), contactProfile.getId());
    }

    private Profile createProfile(UUID userId) {
        User user = User.createUser(userId, "user" + userId + "@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        String profileName = "User " + userId.toString().substring(0, 8);
        return Profile.createProfile(ownerInfo, profileName);
    }

    private Contact createContact(Profile owner, Profile contactProfile) {
        return Contact.createContact(owner, contactProfile);
    }

}