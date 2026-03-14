package com.example.chat.unit.application.service.profile.service;

import com.example.chat.application.data.request.data.PageData;
import com.example.chat.application.mapper.ProfileMapper;
import com.example.chat.application.service.profile.ProfileServiceImpl;
import com.example.chat.application.service.profile.data.request.GetProfilePaginationRequest;
import com.example.chat.application.service.profile.data.request.data.ProfileSearchCriteria;
import com.example.chat.application.service.profile.data.request.data.enums.ProfileOrderBy;
import com.example.chat.application.service.profile.data.response.data.ProfilePaginationData;
import com.example.chat.application.service.profile.helper.specification.ProfileSpecificationBuilder;
import com.example.chat.domain.entity.Profile;
import com.example.chat.infrastructure.repository.ProfileRepository;
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

import java.util.List;

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
    private ProfileSpecificationBuilder specificationBuilder;

    @Mock
    private ProfileMapper profileMapper;

    @InjectMocks
    private ProfileServiceImpl profileService;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    private PageData defaultPageData;
    private Page<Profile> mockProfilePage;
    private ProfilePaginationData mockPaginationData;
    private Specification<Profile> mockSpecification;
    private GetProfilePaginationRequest.GetProfilePaginationRequestBuilder requestBuilder;

    @BeforeEach
    void setUp() {
        defaultPageData = PageData.builder()
                .page(0)
                .size(10)
                .build();

        requestBuilder = GetProfilePaginationRequest.builder()
                .pageData(defaultPageData);

        mockSpecification = mock(Specification.class);
        mockProfilePage = new PageImpl<>(List.of(mock(Profile.class), mock(Profile.class)));
        mockPaginationData = mock(ProfilePaginationData.class);

        when(specificationBuilder.fromCriteria(any())).thenReturn(mockSpecification);
        when(profileRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(mockProfilePage);
        when(profileMapper.toPaginationData(any(Profile.class))).thenReturn(mockPaginationData);
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
    void shouldMapEachProfileToPaginationData() {
        // Arrange
        GetProfilePaginationRequest request = createRequest();

        // Act
        profileService.getProfilePagination(request);

        // Assert
        verify(profileMapper, times(mockProfilePage.getContent().size()))
                .toPaginationData(any(Profile.class));
    }
    @Test
    void shouldReturnPageWithMappedData() {
        // Arrange
        GetProfilePaginationRequest request = createRequest();

        // Act
        var response = profileService.getProfilePagination(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getPage());
        assertEquals(mockProfilePage.getContent().size(), response.getPage().getContent().size());
        response.getPage().getContent().forEach(data -> assertEquals(mockPaginationData, data));
    }
    @Test
    void shouldWorkWhenRepositoryReturnsEmptyPage() {
        // Arrange
        when(profileRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        GetProfilePaginationRequest request = createRequest();

        // Act
        var response = profileService.getProfilePagination(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.getPage().isEmpty());
        verify(profileMapper, never()).toPaginationData(any(Profile.class));
    }

    private GetProfilePaginationRequest createRequest() {
        ProfileSearchCriteria criteria = createCriteria();
        PageData pageData = PageData.builder()
                .page(0)
                .size(1).build();
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

}