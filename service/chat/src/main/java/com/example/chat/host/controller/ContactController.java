package com.example.chat.host.controller;

import com.example.chat.application.service.contact.ContactService;
import com.example.chat.application.service.contact.data.request.ChangeContactNameRequest;
import com.example.chat.application.service.contact.data.request.CreateContactRequest;
import com.example.chat.application.service.contact.data.request.GetContactsRequest;
import com.example.chat.application.service.contact.data.response.ChangeContactNameResponse;
import com.example.chat.application.service.contact.data.response.CreateContactResponse;
import com.example.chat.application.service.contact.data.response.GetContactsResponse;
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

    @PatchMapping("/{contactId}/name")
    public ResponseEntity<ChangeContactNameResponse> changeContactName(
            @PathVariable UUID contactId,
            @Valid @RequestBody ChangeContactNameRequest request) {
        request.setContactId(contactId);
        ChangeContactNameResponse response = contactService.changeContactName(request);
        return ResponseEntity.ok(response);
    }

}