package com.example.chat.application.service.profile.validator;

import com.example.chat.application.exception.AccessDeniedException;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.domain.entity.Profile;
import com.example.chat.infrastructure.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProfileAccessValidatorImpl implements ProfileAccessValidator {

    private final SecurityService securityService;

    public void validateOwnership(Profile profile) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();

        if (!profile.isOwnedByUserId(currentUserId)) {
            throw new AccessDeniedException("You don't have permission to modify this profile");
        }
    }

    public void validateProfileExists(Profile profile, UUID profileId) {
        if (profile == null) {
            throw new ResourceNotFoundException("Profile not found with id: " + profileId);
        }
    }

}