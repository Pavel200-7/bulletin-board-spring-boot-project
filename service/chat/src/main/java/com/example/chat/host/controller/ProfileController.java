package com.example.chat.host.controller;

import com.example.chat.application.service.profile.ProfileService;
import com.example.chat.application.service.profile.data.request.*;
import com.example.chat.application.service.profile.data.response.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{id}")
    public ResponseEntity<GetProfileResponse> getProfile(@PathVariable UUID id) {
        GetProfileResponse response = profileService.getProfile(new GetProfileRequest(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<GetProfileByUserIdResponse> getProfileByUserId(@PathVariable UUID userId) {
        GetProfileByUserIdResponse response = profileService.getProfileByUserId(new GetProfileByUserIdRequest(userId));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<GetProfilePaginationResponse> getProfilePagination(
            @Valid @RequestBody GetProfilePaginationRequest request) {
        GetProfilePaginationResponse response = profileService.getProfilePagination(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CreateProfileResponse> createProfile(
            @Valid @RequestBody CreateProfileRequest request) {
        CreateProfileResponse response = profileService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/public-name")
    public ResponseEntity<ChangePublicNameResponse> changePublicName(
            @Valid @RequestBody ChangePublicNameRequest request) {
        ChangePublicNameResponse response = profileService.changePublicName(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/description")
    public ResponseEntity<ChangeDescriptionResponse> changeDescription(
            @Valid @RequestBody ChangeDescriptionRequest request) {
        ChangeDescriptionResponse response = profileService.changeDescription(request);
        return ResponseEntity.ok(response);
    }

}