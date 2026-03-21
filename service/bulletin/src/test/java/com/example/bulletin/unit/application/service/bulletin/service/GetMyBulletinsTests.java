package com.example.bulletin.unit.application.service.bulletin.service;

import com.example.bulletin.application.mapper.BulletinMapper;
import com.example.bulletin.application.service.bulletin.BulletinServiceImpl;
import com.example.bulletin.application.service.bulletin.data.request.GetMyBulletinsRequest;
import com.example.bulletin.application.service.bulletin.data.request.data.PageData;
import com.example.bulletin.application.service.bulletin.helper.specification.BulletinSpecificationBuilderImpl;
import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import com.example.bulletin.infrastructure.repository.BulletinRepository;
import com.example.bulletin.infrastructure.security.SecurityService;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GetMyBulletinsTests {

    @Mock
    private BulletinRepository bulletinRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private BulletinSpecificationBuilderImpl specificationBuilder;

    @Mock
    private BulletinMapper mapper;

    @InjectMocks
    private BulletinServiceImpl bulletinService;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;




    private UUID currentUserId;
    private Page<Bulletin> bulletinPage;
    private List<Bulletin> bulletins;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        Bulletin bulletin1 = mock(Bulletin.class);
        Bulletin bulletin2 = mock(Bulletin.class);
        bulletins = List.of(bulletin1, bulletin2);
        bulletinPage = new PageImpl<>(bulletins);

        when(securityService.getCurrentUserIdAsUUID()).thenReturn(currentUserId);
        when(specificationBuilder.forCurrentUser(any(), any(), any()))
                .thenReturn(createEmptySpecification());
        when(bulletinRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(bulletinPage);
    }

    private Specification<Bulletin> createEmptySpecification() {
        return (root, query, cb) -> cb.conjunction();
    }

    @Test
    void shouldGetMyBulletinsSuccessfully() {
        // Arrange
        PageData pageData = PageData.builder().page(0).size(20).build();
        GetMyBulletinsRequest request = GetMyBulletinsRequest.builder()
                .pageData(pageData)
                .state(null)
                .title(null)
                .build();

        // Act
        var response = bulletinService.getMyBulletins(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getPage());
        assertEquals(2, response.getPage().getContent().size());

        verify(securityService).getCurrentUserIdAsUUID();
        verify(specificationBuilder).forCurrentUser(currentUserId, null, null);
        verify(bulletinRepository).findAll(any(Specification.class), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(0, capturedPageable.getPageNumber());
        assertEquals(20, capturedPageable.getPageSize());
    }

    @Test
    void shouldFilterByState() {
        // Arrange
        PageData pageData = PageData.builder().page(0).size(20).build();
        GetMyBulletinsRequest request = GetMyBulletinsRequest.builder()
                .pageData(pageData)
                .state(BulletinState.PUBLISHED)
                .title(null)
                .build();

        // Act
        bulletinService.getMyBulletins(request);

        // Assert
        verify(specificationBuilder).forCurrentUser(currentUserId, BulletinState.PUBLISHED, null);
    }

    @Test
    void shouldFilterByTitle() {
        // Arrange
        PageData pageData = PageData.builder().page(0).size(20).build();
        String searchTitle = "test";
        GetMyBulletinsRequest request = GetMyBulletinsRequest.builder()
                .pageData(pageData)
                .state(null)
                .title(searchTitle)
                .build();

        // Act
        bulletinService.getMyBulletins(request);

        // Assert
        verify(specificationBuilder).forCurrentUser(currentUserId, null, searchTitle);
    }

    @Test
    void shouldFilterByStateAndTitle() {
        // Arrange
        PageData pageData = PageData.builder().page(0).size(20).build();
        GetMyBulletinsRequest request = GetMyBulletinsRequest.builder()
                .pageData(pageData)
                .state(BulletinState.MODIFIABLE)
                .title("test")
                .build();

        // Act
        bulletinService.getMyBulletins(request);

        // Assert
        verify(specificationBuilder).forCurrentUser(currentUserId, BulletinState.MODIFIABLE, "test");
    }

    @Test
    void shouldUseCorrectPageAndSize() {
        // Arrange
        int page = 2;
        int size = 15;
        PageData pageData = PageData.builder().page(page).size(size).build();
        GetMyBulletinsRequest request = GetMyBulletinsRequest.builder()
                .pageData(pageData)
                .state(null)
                .title(null)
                .build();

        // Act
        bulletinService.getMyBulletins(request);

        // Assert
        verify(bulletinRepository).findAll(any(Specification.class), pageableCaptor.capture());
        Pageable capturedPageable = pageableCaptor.getValue();

        assertEquals(page, capturedPageable.getPageNumber());
        assertEquals(size, capturedPageable.getPageSize());
    }

    @Test
    void shouldReturnEmptyPageWhenNoBulletins() {
        // Arrange
        PageData pageData = PageData.builder().page(0).size(20).build();
        GetMyBulletinsRequest request = GetMyBulletinsRequest.builder()
                .pageData(pageData)
                .state(null)
                .title(null)
                .build();

        Page<Bulletin> emptyPage = Page.empty();
        when(bulletinRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        // Act
        var response = bulletinService.getMyBulletins(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.getPage().isEmpty());
        assertEquals(0, response.getPage().getTotalElements());
    }

    @Test
    void shouldMapBulletinsToPaginationData() {
        // Arrange
        PageData pageData = PageData.builder().page(0).size(20).build();
        GetMyBulletinsRequest request = GetMyBulletinsRequest.builder()
                .pageData(pageData)
                .state(null)
                .title(null)
                .build();

        // Act
        var response = bulletinService.getMyBulletins(request);

        // Assert
        assertNotNull(response.getPage());
        assertEquals(2, response.getPage().getContent().size());
    }

}