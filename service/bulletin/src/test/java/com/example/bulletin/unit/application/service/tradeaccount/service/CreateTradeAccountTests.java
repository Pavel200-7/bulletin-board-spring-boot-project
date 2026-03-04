package com.example.bulletin.unit.application.service.tradeaccount.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.TradeAccountMapper;
import com.example.bulletin.application.service.tradeaccount.TradeAccountServiceImpl;
import com.example.bulletin.application.service.tradeaccount.data.request.CreateTradeAccountRequest;
import com.example.bulletin.application.service.tradeaccount.data.response.CreateTradeAccountResponse;
import com.example.bulletin.application.data.response.TradeAccountResponse;
import com.example.bulletin.domain.entity.TradeAccount;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.infrastructure.repository.TradeAccountRepository;
import com.example.bulletin.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CreateTradeAccountTests {

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

    @Captor
    private ArgumentCaptor<TradeAccount> tradeAccountCaptor;

    private User user = null;


    @BeforeEach
    public void setup() {
        when(tradeAccountRepository.save(any(TradeAccount.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(mapper.toResponse(any(TradeAccount.class)))
                .thenAnswer(invocation -> {
                    TradeAccount account = invocation.getArgument(0);
                    return mapperHelper.toResponse(account);
                });
    }

    @Test
    public void shouldThrowWhenUserNotFound() {
        // Arrange
        CreateTradeAccountRequest request = createRequest();
        when(userRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.createTradeAccount(request));
    }

    @Test
    public void shouldCreateTradeAccountAndSave() {
        // Arrange
        CreateTradeAccountRequest request = createRequest();
        User user = createUser();
        when(userRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(user));

        // Act
        service.createTradeAccount(request);

        // Assert
        verify(tradeAccountRepository).save(tradeAccountCaptor.capture());
        TradeAccount actual = tradeAccountCaptor.getValue();

        assertThat(actual.getOwnerInfo().getOwnerId()).isEqualTo(user.getId());
        assertThat(actual.isApproved()).isFalse();
        assertThat(actual.getName()).isNull();
    }

    @Test
    public void shouldMapBeforeReturn() {
        // Arrange
        CreateTradeAccountRequest request = createRequest();
        User user = createUser();
        when(userRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(user));

        TradeAccountResponse expected = TradeAccountResponse.builder()
                .ownerId(user.getId())
                .approved(false)
                .coordinatesExact(false)
                .build();

        // Act
        CreateTradeAccountResponse response = service.createTradeAccount(request);
        TradeAccountResponse actual = response.getTradeAccountResponse();

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    private CreateTradeAccountRequest createRequest() {
        return CreateTradeAccountRequest.builder()
                .ownerId(UUID.randomUUID())
                .build();
    }

    private User createUser() {
        if (user == null) {
            user = User.createUser(UUID.randomUUID(), "owner@example.com");
        }

        return user;
    }

}