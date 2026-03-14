package com.example.chat.application.service.contact;

import com.example.chat.application.service.contact.data.request.*;
import com.example.chat.application.service.contact.data.response.*;

public interface ContactService {
    GetContactsResponse getContacts(GetContactsRequest request);
    CreateContactResponse createContact(CreateContactRequest request);
    ChangeContactNameResponse changeContactName(ChangeContactNameRequest request);
}
