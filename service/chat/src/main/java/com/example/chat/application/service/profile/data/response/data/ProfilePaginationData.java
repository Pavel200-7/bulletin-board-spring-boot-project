package com.example.chat.application.service.profile.data.response.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfilePaginationData {
    private UUID id;
    private UUID ownerId;
    private String publicName;
    private String description;
    private UUID imageId;
}
