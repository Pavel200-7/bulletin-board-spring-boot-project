package com.example.chat.unit.application.service.contact.service;

import com.example.chat.application.data.response.ContactResponse;
import com.example.chat.application.exception.AccessDeniedException;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.mapper.ContactMapper;
import com.example.chat.application.service.contact.ContactServiceImpl;
import com.example.chat.application.service.contact.data.request.GetContactByIdRequest;
import com.example.chat.application.service.contact.data.response.GetContactByIdResponse;
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
public class GetContactByIdTests {

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
    private UUID contactId;
    private Profile currentProfile;
    private Contact contact;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        contactId = UUID.randomUUID();

        currentProfile = createProfile(currentUserId);
        Profile contactProfile = createProfile(UUID.randomUUID());
        contact = createContact(currentProfile, contactProfile);

        when(securityService.getCurrentUserIdAsUUID())
                .thenReturn(currentUserId);
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.of(currentProfile));
        when(contactRepository.findById(contactId))
                .thenReturn(Optional.of(contact));
        when(contactMapper.toResponse(any(Contact.class)))
                .thenAnswer(invocation -> {
                    Contact c = invocation.getArgument(0);
                    return mapperHelper.toResponse(c);
                });
    }

    @Test
    void shouldGetContactByIdSuccessfully() {
        // Arrange
        GetContactByIdRequest request = GetContactByIdRequest.builder()
                .contactId(contactId)
                .build();

        ContactResponse expectedResponse = mapperHelper.toResponse(contact);

        // Act
        GetContactByIdResponse response = contactService.getContactById(request);
        ContactResponse actual = response.getContactResponse();

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(contactRepository).findById(contactId);
        verify(profileRepository).findByOwnerInfoOwnerId(currentUserId);
    }

    @Test
    void shouldThrowWhenCurrentProfileNotFound() {
        // Arrange
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.empty());

        GetContactByIdRequest request = GetContactByIdRequest.builder()
                .contactId(contactId)
                .build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> contactService.getContactById(request));
    }

    @Test
    void shouldThrowWhenContactNotFound() {
        // Arrange
        when(contactRepository.findById(contactId))
                .thenReturn(Optional.empty());

        GetContactByIdRequest request = GetContactByIdRequest.builder()
                .contactId(contactId)
                .build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> contactService.getContactById(request));
    }

    @Test
    void shouldThrowWhenUserDoesNotOwnContact() {
        // Arrange
        Profile otherProfile = createProfile(UUID.randomUUID());
        Profile contactProfile = createProfile(UUID.randomUUID());
        Contact otherContact = createContact(otherProfile, contactProfile);

        when(contactRepository.findById(contactId))
                .thenReturn(Optional.of(otherContact));

        GetContactByIdRequest request = GetContactByIdRequest.builder()
                .contactId(contactId)
                .build();

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> contactService.getContactById(request));
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