package com.example.chat.application.service.profile.data.response;

import com.example.chat.application.data.response.ProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetExistsMyProfileResponse {
    private boolean exists;
    private ProfileResponse profileResponse;
}
