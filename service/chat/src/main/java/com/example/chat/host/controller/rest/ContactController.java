package com.example.chat.host.controller.rest;

import com.example.chat.application.service.contact.ContactService;
import com.example.chat.application.service.contact.data.request.*;
import com.example.chat.application.service.contact.data.response.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @GetMapping("/{contactId}")
    public ResponseEntity<GetContactByIdResponse> getContactById(@PathVariable UUID contactId) {
        GetContactByIdRequest request = GetContactByIdRequest.builder()
                .contactId(contactId)
                .build();
        return ResponseEntity.ok(contactService.getContactById(request));
    }

    @GetMapping("/by-profile/{profileId}")
    public ResponseEntity<GetContactByProfileIdResponse> getContactByProfileId(@PathVariable UUID profileId) {
        GetContactByProfileIdRequest request = GetContactByProfileIdRequest.builder()
                .profileId(profileId)
                .build();
        return ResponseEntity.ok(contactService.getContactByProfileId(request));
    }


    @GetMapping
    public ResponseEntity<GetContactsResponse> getContacts() {
        GetContactsResponse response = contactService.getContacts(new GetContactsRequest());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CreateContactResponse> createContact(@Valid @RequestBody CreateContactRequest request) {
        CreateContactResponse response = contactService.createContact(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{contactId}/name")
    public ResponseEntity<ChangeContactNameResponse> changeContactName(
            @PathVariable UUID contactId,
            @Valid @RequestBody ChangeContactNameRequest request) {
        request.setContactId(contactId);
        ChangeContactNameResponse response = contactService.changeContactName(request);
        return ResponseEntity.ok(response);
    }

}