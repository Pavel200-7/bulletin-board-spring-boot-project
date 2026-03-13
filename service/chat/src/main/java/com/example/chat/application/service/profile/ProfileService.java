package com.example.chat.application.service.profile;

import com.example.chat.application.service.profile.data.request.*;
import com.example.chat.application.service.profile.data.response.*;

public interface ProfileService {
    GetProfileResponse getProfile(GetProfileRequest request);
    GetProfilePaginationResponse getProfilePagination(GetProfilePaginationRequest request);
    CreateProfileResponse createProfile(CreateProfileRequest request);
    ChangePublicNameResponse changeName(ChangePublicNameRequest request);
    ChangeDescriptionResponse changeDescription(ChangeDescriptionRequest request);
}
