package com.example.chat.unit.application.service.contact.service;


import com.example.chat.application.data.response.ContactResponse;
import com.example.chat.application.exception.AccessDeniedException;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.mapper.ContactMapper;
import com.example.chat.application.service.contact.ContactServiceImpl;
import com.example.chat.application.service.contact.data.request.GetContactsRequest;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GetContactsTests {

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

    private ContactMapper mapperHelper = Mappers.getMapper(
            ContactMapper.class);

    private UUID currentUserId;
    private UUID ownerProfileId;
    private Profile ownerProfile;
    private List<Contact> mockContacts;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        ownerProfileId = UUID.randomUUID();

        ownerProfile = createProfile(currentUserId);

        when(securityService.getCurrentUserIdAsUUID())
                .thenReturn(currentUserId);

        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.of(ownerProfile));

        mockContacts = List.of(mock(Contact.class), mock(Contact.class));
        when(contactRepository.findByOwnerProfileId(ownerProfile.getId()))
                .thenReturn(List.of(createContact(ownerProfile)));

        when(contactMapper.toResponse(any(Contact.class)))
                .thenAnswer(invocation -> {
                    Contact contact = invocation.getArgument(0);
                    return mapperHelper.toResponse(contact);
                });
    }

    private Contact createContact(Profile ownerProfile) {
        UUID otherProfileId = UUID.randomUUID();
        Profile otherProfile = createProfile(otherProfileId);
        return ownerProfile.addContact(otherProfile);
    }

    @Test
    void shouldGetContactsSuccessfully() {
        // Arrange
        GetContactsRequest request = createRequest();

        // Act
        var response = contactService.getContacts(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getContacts());

        verify(profileRepository).findByOwnerInfoOwnerId(currentUserId);
        verify(contactRepository).findByOwnerProfileId(any(UUID.class));
        verify(contactMapper, times(1)).toResponse(any(Contact.class));
    }

    @Test
    void shouldThrowWhenProfileNotFound() {
        // Arrange
        GetContactsRequest request = createRequest();
        when(profileRepository.findByOwnerInfoOwnerId(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> contactService.getContacts(request));
    }

    @Test
    void shouldThrowWhenUserDoesNotOwnProfile() {
        // Arrange
        GetContactsRequest request = createRequest();
        UUID otherUserId = UUID.randomUUID();
        Profile otherProfile = createProfile(otherUserId);

        when(profileRepository.findByOwnerInfoOwnerId(any()))
                .thenReturn(Optional.of(otherProfile));

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> contactService.getContacts(request));
    }

    private GetContactsRequest createRequest() {
        return GetContactsRequest.builder().build();
    }

    private Profile createProfile(UUID userId) {
        User user = User.createUser(userId, "user" + userId + "@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        String profileName = "User " + userId.toString().substring(0, 8);
        return Profile.createProfile(ownerInfo, profileName);
    }

}