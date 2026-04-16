package com.example.bulletin.integration.hosts;

import com.example.bulletin.application.data.request.BulletinRequest;
import com.example.bulletin.application.data.response.BulletinResponse;
import com.example.bulletin.application.service.bulletin.data.request.UpdateBulletinRequest;
import com.example.bulletin.application.service.bulletin.data.response.CreateBulletinResponse;
import com.example.bulletin.application.service.bulletin.data.response.UpdateBulletinResponse;
import com.example.bulletin.config.TestConfig;
import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.TradeAccount;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import com.example.bulletin.infrastructure.repository.BulletinRepository;
import com.example.bulletin.infrastructure.repository.TradeAccountRepository;
import com.example.bulletin.infrastructure.security.SecurityService;
import com.example.bulletin.integration.hosts.helper.cleaner.DatabaseCleaner;
import com.example.bulletin.integration.hosts.helper.client.BulletinAPIClient;
import com.example.bulletin.integration.hosts.helper.initializer.BulletinInitializer;
import com.example.bulletin.integration.hosts.helper.initializer.CategoryInitializer;
import com.example.bulletin.integration.hosts.helper.initializer.TradeAccountInitializer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
@ActiveProfiles("test")
public class BulletinAPITests {

    @LocalServerPort
    protected int port;

    @MockitoBean
    private SecurityService securityService;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private TradeAccountInitializer tradeAccountInitializer;

    @Autowired
    private BulletinInitializer bulletinInitializer;

    @Autowired
    private CategoryInitializer categoryInitializer;

    private final UUID currentUserId = UUID.randomUUID();
    private BulletinAPIClient bulletinAPIClient;

    @Container
    protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    protected void setUp() {
        bulletinAPIClient = new BulletinAPIClient(port);
        databaseCleaner.cleanAll();
        when(securityService.getCurrentUserIdAsUUID())
                .thenReturn(currentUserId);
    }

    @Test
    public void ShouldCreateDraft() {
        // Arrange
        TradeAccount tradeAccount = tradeAccountInitializer.createApprovedTradeAccount(currentUserId);

        // Act
        CreateBulletinResponse response = bulletinAPIClient.createDraft();

        // Assert
        assertNotNull(response);

        BulletinResponse bulletinResponse = response.getBulletinResponse();
        assertNotNull(bulletinResponse.getId());
        assertEquals(BulletinState.MODIFIABLE, bulletinResponse.getState());
        assertEquals(tradeAccount.getOwner().getId(), bulletinResponse.getOwnerId());
    }

    @Test
    public void ShouldUpdateBulletin() {
        // Arrange
        TradeAccount tradeAccount = tradeAccountInitializer.createApprovedTradeAccount(currentUserId);
        Category category = categoryInitializer.createLeafCategory();
        Bulletin bulletin = bulletinInitializer.createDraft(tradeAccount);

        BulletinRequest bulletinRequest = createBulletinRequest(bulletin, category);
        UpdateBulletinRequest request = UpdateBulletinRequest.builder()
                .bulletinRequest(bulletinRequest)
                .build();

        // Act
        UpdateBulletinResponse response = bulletinAPIClient.updateBulletin(request);

        // Assert
        assertNotNull(response);

        BulletinResponse bulletinResponse = response.getBulletinResponse();
        assertEquals(bulletinRequest.getTitle(), bulletinResponse.getTitle());
        assertEquals(bulletinRequest.getPrice(), bulletinResponse.getPrice());
        assertEquals(bulletinRequest.getDescription(), bulletinResponse.getDescription());
        assertEquals(category.getId(), bulletinResponse.getCategory().getId());
    }

    private BulletinRequest createBulletinRequest(Bulletin bulletin, Category category) {
         return BulletinRequest.builder()
                .id(bulletin.getId())
                .title("title")
                .price(10.0)
                .description("description")
                .categoryId(category.getId())
                .build();
    }

}
