package com.example.chat.application.service.contact;


import com.example.chat.application.data.response.ChatRoomResponse;
import com.example.chat.application.data.response.ContactResponse;
import com.example.chat.application.exception.AccessDeniedException;
import com.example.chat.application.exception.DuplicateResourceException;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.mapper.ContactMapper;
import com.example.chat.application.service.contact.data.request.ChangeContactNameRequest;
import com.example.chat.application.service.contact.data.request.CreateContactRequest;
import com.example.chat.application.service.contact.data.request.GetContactsRequest;
import com.example.chat.application.service.contact.data.response.ChangeContactNameResponse;
import com.example.chat.application.service.contact.data.response.CreateContactResponse;
import com.example.chat.application.service.contact.data.response.GetContactsResponse;
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

        log.info("Начало создания");
        Contact contact = ownerProfile.addContact(contactProfile);
        log.info("Извлекаем newChatRoom");
        ChatRoom newChatRoom = ownerProfile.addChatRoom(contact);

        log.info("new chat {}", newChatRoom.toString());
        chatRoomRepository.save(newChatRoom);

        log.info("profile {}", ownerProfile.toString());
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

}