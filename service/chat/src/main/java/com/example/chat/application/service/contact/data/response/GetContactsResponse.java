package com.example.chat.application.service.contact.data.response;

import com.example.chat.application.data.response.ContactResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetContactsResponse {
    private List<ContactResponse> contacts;
}
