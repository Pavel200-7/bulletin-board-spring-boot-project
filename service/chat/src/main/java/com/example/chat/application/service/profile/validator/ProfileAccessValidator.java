package com.example.chat.application.service.profile.validator;

import com.example.chat.domain.entity.Profile;

import java.util.UUID;

public interface ProfileAccessValidator {
    void validateOwnership(Profile profile);
    void validateProfileExists(Profile profile, UUID profileId);
}
