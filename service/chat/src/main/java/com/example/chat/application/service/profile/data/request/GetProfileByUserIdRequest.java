package com.example.chat.application.service.profile.data.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetProfileByUserIdRequest {
    private UUID id; // Используется именно id от User
}
