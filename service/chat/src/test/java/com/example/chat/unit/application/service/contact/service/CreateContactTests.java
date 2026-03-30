//package com.example.chat.unit.application.service.contact.service;
//
//import com.example.chat.application.data.response.ContactResponse;
//import com.example.chat.application.exception.DuplicateResourceException;
//import com.example.chat.application.exception.ResourceNotFoundException;
//import com.example.chat.application.mapper.ContactMapper;
//import com.example.chat.application.service.contact.ContactServiceImpl;
//import com.example.chat.application.service.contact.data.request.CreateContactRequest;
//import com.example.chat.domain.entity.ChatParticipant;
//import com.example.chat.domain.entity.ChatRoom;
//import com.example.chat.domain.entity.Contact;
//import com.example.chat.domain.entity.Profile;
//import com.example.chat.domain.entity.base.OwnerInfo;
//import com.example.chat.domain.entity.base.user.User;
//import com.example.chat.infrastructure.repository.ChatRoomRepository;
//import com.example.chat.infrastructure.repository.ContactRepository;
//import com.example.chat.infrastructure.repository.ProfileRepository;
//import com.example.chat.infrastructure.security.SecurityService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mapstruct.factory.Mappers;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.mockito.junit.jupiter.MockitoSettings;
//import org.mockito.quality.Strictness;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ActiveProfiles("test")
//@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
//public class CreateContactTests {
//
//    @Mock
//    private ContactRepository contactRepository;
//
//    @Mock
//    private ProfileRepository profileRepository;
//
//    @Mock
//    private ChatRoomRepository chatRoomRepository;
//
//    @Mock
//    private SecurityService securityService;
//
//    @Mock
//    private ContactMapper contactMapper;
//
//    @Mock
//    private Profile ownerProfile;
//
//    @Mock
//    private Contact createdContact;
//
//    @Mock
//    private ChatParticipant mockParticipant;
//
//    @Mock
//    private ChatRoom mockChatRoom;
//
//    @InjectMocks
//    private ContactServiceImpl contactService;
//
//    @Captor
//    private ArgumentCaptor<ChatRoom> chatRoomCaptor;
//
//    private UUID currentUserId;
//    private UUID ownerProfileId;
//    private UUID contactProfileId;
//    private Profile contactProfile;
//    private ContactResponse mockResponse;
//
//    @BeforeEach
//    void setUp() {
//        currentUserId = UUID.randomUUID();
//        ownerProfileId = UUID.randomUUID();
//        contactProfileId = UUID.randomUUID();
//
//        contactProfile = createProfile(UUID.randomUUID());
//        mockResponse = mock(ContactResponse.class);
//        createdContact = mock(Contact.class);
//
//        // Настройка цепочки для получения ChatRoom из ownerProfile
//        when(ownerProfile.getChatParticipants()).thenReturn(List.of(mockParticipant));
//        when(mockParticipant.getChatRoom()).thenReturn(mockChatRoom);
//
//        when(securityService.getCurrentUserIdAsUUID()).thenReturn(currentUserId);
//        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
//                .thenReturn(Optional.of(ownerProfile));
//        when(profileRepository.findById(contactProfileId))
//                .thenReturn(Optional.of(contactProfile));
//        when(ownerProfile.addContact(contactProfile)).thenReturn(createdContact);
//        when(contactRepository.existsByOwnerProfileIdAndContactProfileId(any(), any()))
//                .thenReturn(false);
//        when(contactRepository.save(any(Contact.class)))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//        when(contactMapper.toResponse(any(Contact.class)))
//                .thenReturn(mockResponse);
//        when(createdContact.getId()).thenReturn(UUID.randomUUID());
//    }
//
//    @Test
//    void shouldCreateContactSuccessfully() {
//        // Arrange
//        CreateContactRequest request = createRequest(contactProfileId);
//
//        // Act
//        var response = contactService.createContact(request);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(mockResponse, response.getContactResponse());
//
//        verify(ownerProfile).addContact(contactProfile);
//        verify(profileRepository).save(ownerProfile);
//        // Проверяем, что chatRoom сохранен с правильным объектом
//        verify(chatRoomRepository).save(chatRoomCaptor.capture());
//        assertEquals(mockChatRoom, chatRoomCaptor.getValue());
//        verify(contactMapper).toResponse(createdContact);
//    }
//
//    @Test
//    void shouldThrowWhenOwnerProfileNotFound() {
//        // Arrange
//        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
//                .thenReturn(Optional.empty());
//
//        CreateContactRequest request = createRequest(contactProfileId);
//
//        // Act & Assert
//        assertThrows(ResourceNotFoundException.class,
//                () -> contactService.createContact(request));
//
//        verify(ownerProfile, never()).addContact(any());
//        verify(profileRepository, never()).save(any());
//        verify(chatRoomRepository, never()).save(any());
//    }
//
//    @Test
//    void shouldThrowWhenContactProfileNotFound() {
//        // Arrange
//        when(profileRepository.findById(contactProfileId))
//                .thenReturn(Optional.empty());
//
//        CreateContactRequest request = createRequest(contactProfileId);
//
//        // Act & Assert
//        assertThrows(ResourceNotFoundException.class,
//                () -> contactService.createContact(request));
//
//        verify(ownerProfile, never()).addContact(any());
//        verify(profileRepository, never()).save(any());
//        verify(chatRoomRepository, never()).save(any());
//    }
//
//    @Test
//    void shouldCallAddContactWithCorrectProfile() {
//        // Arrange
//        CreateContactRequest request = createRequest(contactProfileId);
//
//        // Act
//        contactService.createContact(request);
//
//        // Assert
//        verify(ownerProfile).addContact(contactProfile);
//    }
//
//    @Test
//    void shouldSaveOwnerProfile() {
//        // Arrange
//        CreateContactRequest request = createRequest(contactProfileId);
//
//        // Act
//        contactService.createContact(request);
//
//        // Assert
//        verify(profileRepository).save(ownerProfile);
//    }
//
//    @Test
//    void shouldSaveChatRoom() {
//        // Arrange
//        CreateContactRequest request = createRequest(contactProfileId);
//
//        // Act
//        contactService.createContact(request);
//
//        // Assert
//        verify(chatRoomRepository).save(mockChatRoom);
//    }
//
//    @Test
//    void shouldReturnMappedResponse() {
//        // Arrange
//        CreateContactRequest request = createRequest(contactProfileId);
//
//        // Act
//        var response = contactService.createContact(request);
//
//        // Assert
//        assertEquals(mockResponse, response.getContactResponse());
//        verify(contactMapper).toResponse(createdContact);
//    }
//
//    private CreateContactRequest createRequest(UUID profileId) {
//        return CreateContactRequest.builder()
//                .profileId(profileId)
//                .build();
//    }
//
//    private Profile createProfile(UUID userId) {
//        User user = User.createUser(userId, "user" + userId + "@example.com");
//        OwnerInfo ownerInfo = new OwnerInfo(user);
//        String profileName = "User " + userId.toString().substring(0, 8);
//        return Profile.createProfile(ownerInfo, profileName);
//    }
//
//}