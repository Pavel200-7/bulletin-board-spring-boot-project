package com.example.chat.unit.application.service.contact.service;

import com.example.chat.application.data.response.ContactResponse;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.mapper.ContactMapper;
import com.example.chat.application.service.contact.ContactServiceImpl;
import com.example.chat.application.service.contact.data.request.CreateContactRequest;
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
public class CreateContactTests {

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

    @Captor
    private ArgumentCaptor<Contact> contactCaptor;

    private ContactMapper mapperHelper = Mappers.getMapper(
            ContactMapper.class);

    private UUID currentUserId;
    private UUID ownerProfileId;
    private UUID contactProfileId;
    private Profile ownerProfile;
    private Profile contactProfile;
    private ContactResponse mockResponse;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        ownerProfileId = UUID.randomUUID();
        contactProfileId = UUID.randomUUID();

        ownerProfile = createProfile(currentUserId);
        contactProfile = createProfile(UUID.randomUUID());

        mockResponse = mock(ContactResponse.class);

        when(securityService.getCurrentUserIdAsUUID()).thenReturn(currentUserId);
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.of(ownerProfile));
        when(profileRepository.findById(contactProfileId))
                .thenReturn(Optional.of(contactProfile));
        when(contactRepository.existsByOwnerProfileIdAndContactProfileId(any(), any()))
                .thenReturn(false);
        when(contactRepository.save(any(Contact.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(contactMapper.toResponse(any(Contact.class)))
                .thenAnswer(invocation -> {
                    Contact contact = invocation.getArgument(0);
                    return mapperHelper.toResponse(contact);
                });
    }

    @Test
    void shouldCreateContactSuccessfully() {
        // Arrange
        CreateContactRequest request = createRequest(contactProfileId);

        // Act
        var response = contactService.createContact(request);

        // Assert
        assertNotNull(response);

        verify(contactRepository).save(contactCaptor.capture());
        Contact savedContact = contactCaptor.getValue();

        assertEquals(ownerProfile, savedContact.getOwnerProfile());
        assertEquals(contactProfile, savedContact.getContactProfile());
        assertEquals(contactProfile.getPublicName(), savedContact.getContactName());
    }

    @Test
    void shouldThrowWhenOwnerProfileNotFound() {
        // Arrange
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.empty());

        CreateContactRequest request = createRequest(contactProfileId);


        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> contactService.createContact(request));

    }

    @Test
    void shouldThrowWhenContactProfileNotFound() {
        // Arrange
        when(profileRepository.findById(contactProfileId))
                .thenReturn(Optional.empty());

        CreateContactRequest request = createRequest(contactProfileId);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> contactService.createContact(request));
    }

    private CreateContactRequest createRequest(UUID profileId) {
        return CreateContactRequest.builder()
                .profileId(profileId)
                .build();
    }

    private Profile createProfile(UUID userId) {
        User user = User.createUser(userId, "user" + userId + "@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        String profileName = "User " + userId.toString().substring(0, 8);
        return Profile.createProfile(ownerInfo, profileName);
    }

}