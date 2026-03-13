package com.example.chat.application.service.profile.data.response;

import com.example.chat.application.data.response.ProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateProfileResponse {
    private ProfileResponse profileResponse;
}
