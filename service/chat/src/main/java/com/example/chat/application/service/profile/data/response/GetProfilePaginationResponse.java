package com.example.chat.application.service.profile.data.response;

import com.example.chat.application.data.request.data.PageData;
import com.example.chat.application.service.profile.data.request.data.ProfileSearchCriteria;
import com.example.chat.application.service.profile.data.response.data.ProfilePaginationData;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetProfilePaginationResponse {
    private Page<ProfilePaginationData> page;
}
