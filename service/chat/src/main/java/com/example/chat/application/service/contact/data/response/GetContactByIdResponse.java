package com.example.chat.application.service.contact.data.response;

import com.example.chat.application.data.response.ContactResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetContactByIdResponse {
    private ContactResponse contactResponse;
}