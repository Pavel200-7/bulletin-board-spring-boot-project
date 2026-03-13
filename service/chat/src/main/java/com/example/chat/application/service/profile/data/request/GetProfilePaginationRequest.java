package com.example.chat.application.service.profile.data.request;

import com.example.chat.application.data.request.data.PageData;
import com.example.chat.application.service.profile.data.request.data.ProfileSearchCriteria;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetProfilePaginationRequest {
    @Valid
    private PageData pageData;
    private ProfileSearchCriteria criteria;
}
