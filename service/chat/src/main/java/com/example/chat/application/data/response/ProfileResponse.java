package com.example.chat.application.data.response;

import com.example.chat.domain.entity.base.OwnerInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.Delegate;

import java.util.UUID;

@Value
@Builder
public class ProfileResponse {
    private UUID id;
    private UUID ownerId;
    private String publicName;
    private String description;
    private UUID imageId;
}
