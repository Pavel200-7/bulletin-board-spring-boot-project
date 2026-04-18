package com.example.bulletin.unit.application.service.tradeaccount.service;

import com.example.bulletin.application.data.response.TradeAccountResponse;
import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.TradeAccountMapper;
import com.example.bulletin.application.service.tradeaccount.TradeAccountServiceImpl;
import com.example.bulletin.application.service.tradeaccount.data.request.GetMyTradeAccountRequest;
import com.example.bulletin.application.service.tradeaccount.data.response.GetMyTradeAccountResponse;
import com.example.bulletin.domain.entity.TradeAccount;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.infrastructure.repository.TradeAccountRepository;
import com.example.bulletin.infrastructure.security.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GetMyTradeAccountTests {

    private TradeAccountMapper mapperHelper = Mappers.getMapper(TradeAccountMapper.class);

    @Mock
    private TradeAccountRepository tradeAccountRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private TradeAccountMapper mapper;

    @InjectMocks
    private TradeAccountServiceImpl service;

    private UUID currentUserId;
    private TradeAccount tradeAccount;
    private GetMyTradeAccountRequest request;

    @BeforeEach
    public void setup() {
        currentUserId = UUID.randomUUID();
        request = new GetMyTradeAccountRequest();

        tradeAccount = createTradeAccount(currentUserId);

        when(securityService.getCurrentUserIdAsUUID()).thenReturn(currentUserId);
        when(tradeAccountRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.of(tradeAccount));

        when(mapper.toResponse(any(TradeAccount.class)))
                .thenAnswer(invocation -> {
                    TradeAccount account = invocation.getArgument(0);
                    return mapperHelper.toResponse(account);
                });
    }

    @Test
    public void shouldThrowWhenTradeAccountNotFound() {
        // Arrange
        when(tradeAccountRepository.findByOwnerInfoOwnerId(currentUserId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.getMyTradeAccount(request));

        verify(securityService).getCurrentUserIdAsUUID();
        verify(tradeAccountRepository).findByOwnerInfoOwnerId(currentUserId);
    }

    @Test
    public void shouldReturnTradeAccountWhenFound() {
        // Arrange
        TradeAccountResponse expected = mapperHelper.toResponse(tradeAccount);

        // Act
        GetMyTradeAccountResponse response = service.getMyTradeAccount(request);
        TradeAccountResponse actual = response.getTradeAccountResponse();

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);

        verify(securityService).getCurrentUserIdAsUUID();
        verify(tradeAccountRepository).findByOwnerInfoOwnerId(currentUserId);
        verify(mapper).toResponse(tradeAccount);
    }

    @Test
    public void shouldUseCorrectUserIdFromSecurityContext() {
        // Act
        service.getMyTradeAccount(request);

        // Assert
        verify(securityService).getCurrentUserIdAsUUID();
        verify(tradeAccountRepository).findByOwnerInfoOwnerId(currentUserId);
    }

    @Test
    public void shouldReturnCorrectResponseStructure() {
        // Act
        GetMyTradeAccountResponse response = service.getMyTradeAccount(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getTradeAccountResponse()).isNotNull();
        assertThat(response.getTradeAccountResponse().getId()).isEqualTo(tradeAccount.getId());
        assertThat(response.getTradeAccountResponse().getOwnerId()).isEqualTo(currentUserId);
    }

    private TradeAccount createTradeAccount(UUID userId) {
        User user = User.createUser(userId, "owner@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        TradeAccount tradeAccount = TradeAccount.createTradeAccount(ownerInfo);
        tradeAccount.setName("Test Account");
        tradeAccount.setPhone("+79991234567");
        return tradeAccount;
    }

}