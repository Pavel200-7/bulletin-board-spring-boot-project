package com.example.chat.application.data.response;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class ContactResponse {
    private UUID id;
    private UUID ownerId;
    private UUID contactProfileId;
    private String contactName;
    private UUID chatId;
}
