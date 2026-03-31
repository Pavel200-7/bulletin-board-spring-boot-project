package com.example.chat.application.service.contact;


import com.example.chat.application.data.response.ChatRoomResponse;
import com.example.chat.application.data.response.ContactResponse;
import com.example.chat.application.exception.AccessDeniedException;
import com.example.chat.application.exception.DuplicateResourceException;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.mapper.ContactMapper;
import com.example.chat.application.service.contact.data.request.*;
import com.example.chat.application.service.contact.data.response.*;
import com.example.chat.application.service.profile.validator.ProfileAccessValidator;
import com.example.chat.domain.entity.ChatParticipant;
import com.example.chat.domain.entity.ChatRoom;
import com.example.chat.domain.entity.Contact;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.enums.ChatRoomType;
import com.example.chat.infrastructure.repository.ChatRoomRepository;
import com.example.chat.infrastructure.repository.ContactRepository;
import com.example.chat.infrastructure.repository.ProfileRepository;
import com.example.chat.infrastructure.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final ProfileRepository profileRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final SecurityService securityService;
    private final ContactMapper contactMapper;

    @Override
    @Transactional(readOnly = true)
    public GetContactsResponse getContacts(GetContactsRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        Profile profile = profileRepository.findByOwnerInfoOwnerId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user with id: " + currentUserId));

        if (!profile.getOwnerInfo().getOwnerId().equals(currentUserId)) {
            throw new AccessDeniedException("You don't have permission to view contacts of this profile");
        }

        List<Contact> contacts = contactRepository.findByOwnerProfileId(profile.getId());
        log.info("Найдено {} контактов у профиля с id: {}", contacts.size(), profile.getId());

        List<ContactResponse> contactResponses = contacts.stream()
                .map(contactMapper::toResponse)
                .toList();
        return new GetContactsResponse(contactResponses);
    }

    @Override
    @Transactional
    public CreateContactResponse createContact(CreateContactRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        Profile ownerProfile = profileRepository.findByOwnerInfoOwnerId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for current user with id: " + currentUserId));
        Profile contactProfile = profileRepository.findById(request.getProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Contact profile not found with id: " + request.getProfileId()));

        Contact contact = ownerProfile.addContact(contactProfile);
        ChatRoom newChatRoom = ownerProfile.addChatRoom(contact);

        chatRoomRepository.save(newChatRoom);
        profileRepository.save(ownerProfile);

        log.info("Создан новый контакт с id: {}, чат так же создан.", contact.getId());
        
        ContactResponse contactResponse = contactMapper.toResponse(contact);
        return new CreateContactResponse(contactResponse);
    }

    @Override
    @Transactional
    public ChangeContactNameResponse changeContactName(ChangeContactNameRequest request) {
        Contact contact = contactRepository.findById(request.getContactId())
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + request.getContactId()));

        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        if (!contact.getOwnerProfile().getOwnerInfo().getOwnerId().equals(currentUserId)) {
            throw new AccessDeniedException("You don't have permission to modify this contact");
        }

        contact.changeContactName(request.getNewName().trim());
        contactRepository.save(contact);
        log.info("Имя контакта с id {} изменено.", contact.getId());

        ContactResponse contactResponse = contactMapper.toResponse(contact);
        return new ChangeContactNameResponse(contactResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public GetContactByIdResponse getContactById(GetContactByIdRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        Profile currentProfile = profileRepository.findByOwnerInfoOwnerId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for current user with id: " + currentUserId));
        Contact contact = contactRepository.findById(request.getContactId())
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + request.getContactId()));

        if (!contact.getOwnerProfile().getId().equals(currentProfile.getId()) &&
                !contact.getContactProfile().getId().equals(currentProfile.getId())) {
            throw new AccessDeniedException("You don't have permission to view this contact");
        }

        log.info("Найден контакт с id: {} для пользователя: {}", contact.getId(), currentUserId);

        ContactResponse contactResponse = contactMapper.toResponse(contact);
        return new GetContactByIdResponse(contactResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public GetContactByProfileIdResponse getContactByProfileId(GetContactByProfileIdRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        Profile currentProfile = profileRepository.findByOwnerInfoOwnerId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for current user with id: " + currentUserId));
        Profile contactProfile = profileRepository.findById(request.getProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + request.getProfileId()));

        Contact contact = contactRepository.findByProfilesId(currentProfile.getId(), contactProfile.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Contact not found between profiles: %s and %s",
                                currentProfile.getId(), contactProfile.getId())));

        log.info("Найден контакт между профилями {} и {}", currentProfile.getId(), contactProfile.getId());
        ContactResponse contactResponse = contactMapper.toResponse(contact);
        return new GetContactByProfileIdResponse(contactResponse);
    }

}