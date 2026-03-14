package com.example.chat.application.data.response;


import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class ChatResponse {
    private UUID id;
    private String name;
    private List<ContactResponse> contactResponses;
    private UUID imageId;
}
