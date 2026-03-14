package com.example.chat.unit.application.service.contact.service;

import com.example.chat.application.exception.AccessDeniedException;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.service.contact.ContactServiceImpl;
import com.example.chat.application.service.contact.data.request.ChangeContactNameRequest;
import com.example.chat.application.mapper.ContactMapper;
import com.example.chat.domain.entity.Contact;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.entity.base.user.User;
import com.example.chat.infrastructure.repository.ContactRepository;
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
public class ChangeContactNameTests {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private ContactMapper contactMapper;

    @InjectMocks
    private ContactServiceImpl contactService;

    @Captor
    private ArgumentCaptor<Contact> contactCaptor;

    private ContactMapper mapperHelper = Mappers.getMapper(ContactMapper.class);

    private UUID currentUserId;
    private UUID contactId;
    private Contact contact;
    private Profile ownerProfile;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        contactId = UUID.randomUUID();

        ownerProfile = createProfile(currentUserId);
        contact = createContact(ownerProfile, createProfile(UUID.randomUUID()));

        when(securityService.getCurrentUserIdAsUUID()).thenReturn(currentUserId);
        when(contactRepository.findById(contactId))
                .thenReturn(Optional.of(contact));
        when(contactRepository.save(any(Contact.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(contactMapper.toResponse(any(Contact.class)))
                .thenAnswer(invocation -> {
                    Contact contact = invocation.getArgument(0);
                    return mapperHelper.toResponse(contact);
                });
    }

    @Test
    void shouldChangeContactNameSuccessfully() {
        // Arrange
        ChangeContactNameRequest request = createRequest(contactId);

        // Act
        var response = contactService.changeContactName(request);

        // Assert
        assertNotNull(response);
        verify(contactRepository).save(contactCaptor.capture());
        Contact updatedContact = contactCaptor.getValue();

        assertEquals(request.getNewName(), updatedContact.getContactName());
    }

    @Test
    void shouldThrowWhenContactNotFound() {
        // Arrange
        when(contactRepository.findById(contactId))
                .thenReturn(Optional.empty());

        ChangeContactNameRequest request = createRequest(contactId);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> contactService.changeContactName(request));
    }

    @Test
    void shouldThrowWhenUserDoesNotOwnContact() {
        // Arrange
        UUID otherUserId = UUID.randomUUID();
        Profile otherProfile = createProfile(otherUserId);
        Contact otherContact = createContact(otherProfile, createProfile(UUID.randomUUID()));

        when(contactRepository.findById(any()))
                .thenReturn(Optional.of(otherContact));
        ChangeContactNameRequest request = createRequest(UUID.randomUUID());

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> contactService.changeContactName(request));
    }

    private ChangeContactNameRequest createRequest(UUID contactId) {
        return ChangeContactNameRequest.builder()
                .contactId(contactId)
                .newName("New Name")
                .build();

    }

    private Profile createProfile(UUID userId) {
        User user = User.createUser(userId, "user" + userId + "@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        String profileName = "User " + userId.toString().substring(0, 8);
        return Profile.createProfile(ownerInfo, profileName);
    }

    private Contact createContact(Profile owner, Profile contact) {
        return Contact.createContact(owner, contact);
    }
}