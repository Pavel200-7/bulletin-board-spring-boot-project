package com.example.bulletin.unit.application.service.tradeaccount.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.TradeAccountMapper;
import com.example.bulletin.application.service.tradeaccount.TradeAccountServiceImpl;
import com.example.bulletin.application.service.tradeaccount.data.request.GetTradeAccountRequest;
import com.example.bulletin.application.service.tradeaccount.data.response.GetTradeAccountResponse;
import com.example.bulletin.application.service.tradeaccount.data.response.data.TradeAccountResponse;
import com.example.bulletin.domain.entity.TradeAccount;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.infrastructure.repository.TradeAccountRepository;
import com.example.bulletin.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GetTradeAccountTests {

    @Autowired
    private TradeAccountMapper mapperHelper;

    @Mock
    private TradeAccountRepository tradeAccountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TradeAccountMapper mapper;

    @InjectMocks
    private TradeAccountServiceImpl service;

    private User user = null;

    @BeforeEach
    public void setup() {
        TradeAccount tradeAccount = createTradeAccount();

        when(tradeAccountRepository.findById(any(UUID.class)))
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
        GetTradeAccountRequest request = createRequest();
        when(tradeAccountRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.getTradeAccount(request));
    }

    @Test
    public void shouldReturnTradeAccountWhenFound() {
        // Arrange
        GetTradeAccountRequest request = createRequest();
        TradeAccountResponse expected = mapperHelper.toResponse(createTradeAccount());

        // Act
        GetTradeAccountResponse response = service.getTradeAccount(request);
        TradeAccountResponse actual = response.getTradeAccountResponse();

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    private GetTradeAccountRequest createRequest() {
        return GetTradeAccountRequest.builder()
                .id(UUID.randomUUID())
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