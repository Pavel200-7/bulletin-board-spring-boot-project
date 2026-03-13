package com.example.chat.application.service.profile;

import com.example.chat.application.data.request.data.PageData;
import com.example.chat.application.data.response.ProfileResponse;
import com.example.chat.application.exception.DuplicateResourceException;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.mapper.ProfileMapper;
import com.example.chat.application.service.profile.data.request.*;
import com.example.chat.application.service.profile.data.request.data.ProfileSearchCriteria;
import com.example.chat.application.service.profile.data.response.*;
import com.example.chat.application.service.profile.data.response.data.ProfilePaginationData;
import com.example.chat.application.service.profile.helper.specification.ProfileSpecificationBuilder;
import com.example.chat.application.service.profile.validator.ProfileAccessValidator;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.entity.base.user.User;
import com.example.chat.infrastructure.repository.ProfileRepository;
import com.example.chat.infrastructure.repository.UserRepository;
import com.example.chat.infrastructure.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final ProfileMapper profileMapper;
    private final ProfileAccessValidator accessValidator;
    private final ProfileSpecificationBuilder specificationBuilder;


    @Override
    public GetProfileResponse getProfile(GetProfileRequest request) {
        Profile profile = profileRepository.findByOwnerInfoOwnerId(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user with id: " + request.getId()));
        log.info("Найден profile с id: {}", profile.getId());
        ProfileResponse profileResponse = profileMapper.toResponse(profile);
        return new GetProfileResponse(profileResponse);
    }

    @Override
    public GetProfilePaginationResponse getProfilePagination(GetProfilePaginationRequest request) {
        ProfileSearchCriteria criteria = request.getCriteria();
        Specification<Profile> spec = specificationBuilder.fromCriteria(criteria);
        PageData pageData = request.getPageData();

        Sort sort = Sort.by(criteria.getDirection(), criteria.getOrderBy().getFieldName());
        Pageable pageable = PageRequest.of(
                pageData.getPage(),
                pageData.getSize(),
                sort);
        Page<Profile> profiles = profileRepository.findAll(spec, pageable);
        log.info("Найдено {} профилей в текущей странице.", profiles.getSize());

        Page<ProfilePaginationData> paginationData = profiles.map(profileMapper::toPaginationData);
        return new GetProfilePaginationResponse(paginationData);
    }

    @Override
    @Transactional
    public CreateProfileResponse createProfile(CreateProfileRequest request) {
        if (profileRepository.existsByOwnerInfoOwnerId(request.getOwnerId())) {
            throw new DuplicateResourceException("Profile already exists for user: " + request.getOwnerId());
        }

        User user = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getOwnerId()));

        OwnerInfo ownerInfo = new OwnerInfo(user);
        String publicName = request.getOwnerName() != null ? request.getOwnerName() : user.getEmail();

        Profile profile = Profile.createProfile(ownerInfo, publicName);

        profileRepository.save(profile);
        log.info("Создан Profile с id: {}", profile.getId());

        ProfileResponse profileResponse = profileMapper.toResponse(profile);
        return new CreateProfileResponse(profileResponse);
    }

    @Override
    public ChangePublicNameResponse changeName(ChangePublicNameRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        Profile profile = profileRepository.findByOwnerInfoOwnerId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profile not found for current user with id: " + currentUserId));
        accessValidator.validateOwnership(profile);

        String newName = request.getName().trim();
        profile.changePublicName(newName);
        profileRepository.save(profile);
        log.info("Изменено имя: {} для profile с id: {}", newName, profile.getId());

        ProfileResponse profileResponse = profileMapper.toResponse(profile);
        return new ChangePublicNameResponse(profileResponse);
    }

    @Override
    public ChangeDescriptionResponse changeDescription(ChangeDescriptionRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        Profile profile = profileRepository.findByOwnerInfoOwnerId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profile not found for current user with id: " + currentUserId));
        accessValidator.validateOwnership(profile);

        profile.changeDescription(request.getDescription());
        profileRepository.save(profile);
        log.info("Изменено описание для profile: {}", profile.getId());

        ProfileResponse profileResponse = profileMapper.toResponse(profile);
        return new ChangeDescriptionResponse(profileResponse);
    }

}