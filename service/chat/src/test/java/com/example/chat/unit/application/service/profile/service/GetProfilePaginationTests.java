package com.example.chat.unit.application.service.profile.service;

import com.example.chat.application.data.request.data.PageData;
import com.example.chat.application.exception.ResourceNotFoundException;
import com.example.chat.application.mapper.ProfileMapper;
import com.example.chat.application.service.profile.ProfileServiceImpl;
import com.example.chat.application.service.profile.data.request.GetProfilePaginationRequest;
import com.example.chat.application.service.profile.data.request.data.ProfileSearchCriteria;
import com.example.chat.application.service.profile.data.request.data.enums.ProfileOrderBy;
import com.example.chat.application.service.profile.data.response.GetProfilePaginationResponse;
import com.example.chat.application.service.profile.data.response.data.ProfilePaginationData;
import com.example.chat.application.service.profile.helper.specification.ProfileSpecificationBuilder;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.entity.base.user.User;
import com.example.chat.infrastructure.repository.ContactRepository;
import com.example.chat.infrastructure.repository.ProfileRepository;
import com.example.chat.infrastructure.security.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GetProfilePaginationTests {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private ProfileSpecificationBuilder specificationBuilder;

    @Mock
    private ProfileMapper profileMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private ProfileServiceImpl profileService;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    private PageData defaultPageData;
    private Page<Profile> mockProfilePage;
    private ProfilePaginationData mockPaginationData;
    private Specification<Profile> mockSpecification;
    private GetProfilePaginationRequest.GetProfilePaginationRequestBuilder requestBuilder;

    private UUID currentUserId;
    private Profile currentProfile;
    private Set<UUID> contactProfileIds;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        currentProfile = createProfileWithUserId(currentUserId);

        defaultPageData = PageData.builder()
                .page(0)
                .size(10)
                .build();

        requestBuilder = GetProfilePaginationRequest.builder()
                .pageData(defaultPageData);

        when(securityService.getCurrentUserIdAsUUID())
                .thenReturn(currentUserId);

        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.of(currentProfile));

        contactProfileIds = new HashSet<>();
        when(contactRepository.findContactProfileIdsByOwnerProfileId(currentProfile.getId()))
                .thenReturn(contactProfileIds);

        mockSpecification = mock(Specification.class);
        mockProfilePage = new PageImpl<>(List.of(createProfileWithUserId(UUID.randomUUID()), createProfileWithUserId(UUID.randomUUID())));
        mockPaginationData = mock(ProfilePaginationData.class);

        when(specificationBuilder.fromCriteria(any())).thenReturn(mockSpecification);
        when(profileRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(mockProfilePage);
        when(profileMapper.toPaginationData(any(Profile.class), any(boolean.class))).thenReturn(mockPaginationData);
    }

    @Test
    void shouldThrowWhenCurrentProfileNotFound() {
        // Arrange
        when(profileRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.empty());

        GetProfilePaginationRequest request = createRequest();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> profileService.getProfilePagination(request));

        verify(contactRepository, never()).findContactProfileIdsByOwnerProfileId(any());
        verify(profileRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void shouldGetCurrentProfileAndContacts() {
        // Arrange
        GetProfilePaginationRequest request = createRequest();

        // Act
        profileService.getProfilePagination(request);

        // Assert
        verify(securityService).getCurrentUserIdAsUUID();
        verify(profileRepository).findByOwnerInfoOwnerId(currentUserId);
        verify(contactRepository).findContactProfileIdsByOwnerProfileId(currentProfile.getId());
    }

    @Test
    void shouldBuildSpecificationFromCriteria() {
        // Arrange
        GetProfilePaginationRequest request = createRequest();

        // Act
        profileService.getProfilePagination(request);

        // Assert
        verify(specificationBuilder).fromCriteria(any(ProfileSearchCriteria.class));
        verify(profileRepository).findAll(eq(mockSpecification), any(Pageable.class));
    }

    @Test
    void shouldCreatePageableWithCorrectParameters() {
        // Arrange
        ProfileSearchCriteria criteria = ProfileSearchCriteria.builder()
                .publicName("test-name")
                .orderBy(ProfileOrderBy.PUBLIC_NAME)
                .direction(Direction.ASC)
                .build();

        PageData pageData = PageData.builder()
                .page(2)
                .size(20)
                .build();

        GetProfilePaginationRequest request = GetProfilePaginationRequest.builder()
                .criteria(criteria)
                .pageData(pageData)
                .build();

        // Act
        profileService.getProfilePagination(request);

        // Assert
        verify(profileRepository).findAll(any(Specification.class), pageableCaptor.capture());
        Pageable capturedPageable = pageableCaptor.getValue();

        assertEquals(2, capturedPageable.getPageNumber());
        assertEquals(20, capturedPageable.getPageSize());
        assertEquals(Direction.ASC, capturedPageable.getSort().getOrderFor("publicName").getDirection());
    }

    @Test
    void shouldMarkProfilesThatAreContacts() {
        // Arrange
        Profile profile1 = createProfileWithUserId(UUID.randomUUID());
        Profile profile2 = createProfileWithUserId(UUID.randomUUID());
        Profile profile3 = createProfileWithUserId(UUID.randomUUID());

        contactProfileIds.add(profile1.getId());
        contactProfileIds.add(profile3.getId());

        Page<Profile> profilesPage = new PageImpl<>(List.of(profile1, profile2, profile3));
        when(profileRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(profilesPage);

        GetProfilePaginationRequest request = createRequest();

        // Act
        profileService.getProfilePagination(request);

        // Assert
        verify(profileMapper).toPaginationData(profile1, true);
        verify(profileMapper).toPaginationData(profile2, false);
        verify(profileMapper).toPaginationData(profile3, true);
    }

    @Test
    void shouldMapEachProfileToPaginationDataWithContactFlag() {
        // Arrange
        GetProfilePaginationRequest request = createRequest();

        // Act
        profileService.getProfilePagination(request);

        // Assert
        verify(profileMapper, times(mockProfilePage.getContent().size()))
                .toPaginationData(any(Profile.class), any(boolean.class));
    }

    @Test
    void shouldReturnPageWithMappedData() {
        // Arrange
        GetProfilePaginationRequest request = createRequest();

        // Act
        GetProfilePaginationResponse response = profileService.getProfilePagination(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getPage());
        assertEquals(mockProfilePage.getContent().size(), response.getPage().getContent().size());
        response.getPage().getContent().forEach(data -> assertEquals(mockPaginationData, data));
    }

    private GetProfilePaginationRequest createRequest() {
        ProfileSearchCriteria criteria = createCriteria();
        PageData pageData = PageData.builder()
                .page(0)
                .size(1)
                .build();
        return requestBuilder
                .criteria(criteria)
                .pageData(pageData)
                .build();
    }

    private ProfileSearchCriteria createCriteria() {
        return ProfileSearchCriteria.builder()
                .publicName("test-name")
                .orderBy(ProfileOrderBy.PUBLIC_NAME)
                .direction(Direction.ASC)
                .build();
    }

    private Profile createProfileWithUserId(UUID userId) {
        User user = User.createUser(userId, "user@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        Profile profile = Profile.createProfile(ownerInfo, "Test User");
        profile.changeDescription("Test description");
        return profile;
    }

}