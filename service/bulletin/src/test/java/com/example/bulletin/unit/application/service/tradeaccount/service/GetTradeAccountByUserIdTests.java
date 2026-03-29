package com.example.bulletin.unit.application.service.tradeaccount.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.TradeAccountMapper;
import com.example.bulletin.application.service.tradeaccount.TradeAccountServiceImpl;
import com.example.bulletin.application.service.tradeaccount.data.request.GetByUserIdTradeAccountRequest;
import com.example.bulletin.application.service.tradeaccount.data.response.GetByUserIdTradeAccountResponse;
import com.example.bulletin.application.data.response.TradeAccountResponse;
import com.example.bulletin.domain.entity.TradeAccount;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.infrastructure.repository.TradeAccountRepository;
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
public class GetTradeAccountByUserIdTests {

    private TradeAccountMapper mapperHelper = Mappers.getMapper(
            TradeAccountMapper.class);

    @Mock
    private TradeAccountRepository tradeAccountRepository;

    @Mock
    private TradeAccountMapper mapper;

    @InjectMocks
    private TradeAccountServiceImpl service;

    private User user = null;

    @BeforeEach
    public void setup() {
        TradeAccount tradeAccount = createTradeAccount();

        when(tradeAccountRepository.findByOwnerInfo_Owner_Id(any(UUID.class)))
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
        GetByUserIdTradeAccountRequest request = createRequest();
        when(tradeAccountRepository.findByOwnerInfo_Owner_Id(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.getTradeAccountByUserId(request));
    }

    @Test
    public void shouldReturnTradeAccountWhenFound() {
        // Arrange
        GetByUserIdTradeAccountRequest request = createRequest();
        TradeAccountResponse expected = mapperHelper.toResponse(createTradeAccount());

        // Act
        GetByUserIdTradeAccountResponse response = service.getTradeAccountByUserId(request);
        TradeAccountResponse actual = response.getTradeAccountResponse();

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    public void shouldQueryByCorrectUserId() {
        // Arrange
        UUID expectedUserId = UUID.randomUUID();
        GetByUserIdTradeAccountRequest request = GetByUserIdTradeAccountRequest.builder()
                .userId(expectedUserId)
                .build();

        // Act
        service.getTradeAccountByUserId(request);

        // Assert
        verify(tradeAccountRepository).findByOwnerInfo_Owner_Id(expectedUserId);
    }

    private GetByUserIdTradeAccountRequest createRequest() {
        return GetByUserIdTradeAccountRequest.builder()
                .userId(UUID.randomUUID())
                .build();
    }

    private TradeAccount createTradeAccount() {
        User user = createUser();
        OwnerInfo ownerInfo = new OwnerInfo(user);
        TradeAccount tradeAccount = TradeAccount.createTradeAccount(ownerInfo);
        tradeAccount.setName("Test Account");
        tradeAccount.setPhone("+79991234567");
        return tradeAccount;
    }

    private User createUser() {
        if (user == null) {
            user = User.createUser(UUID.randomUUID(), "owner@example.com");
        }

        return user;
    }

}