package com.example.bulletin.unit.application.service.tradeaccount.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.TradeAccountMapper;
import com.example.bulletin.application.service.tradeaccount.TradeAccountServiceImpl;
import com.example.bulletin.application.service.tradeaccount.data.request.SetExactLocationTradeAccountRequest;
import com.example.bulletin.application.service.tradeaccount.data.response.SetExactLocationTradeAccountResponse;
import com.example.bulletin.application.service.tradeaccount.data.response.data.TradeAccountResponse;
import com.example.bulletin.domain.entity.TradeAccount;
import com.example.bulletin.domain.entity.base.OwnerInfo;
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
public class SetExactLocationTests {

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

    private TradeAccount tradeAccount;

    @BeforeEach
    public void setup() {
        tradeAccount = createTradeAccount();

        when(tradeAccountRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(tradeAccount));

        when(tradeAccountRepository.save(any(TradeAccount.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(mapper.toResponse(any(TradeAccount.class)))
                .thenAnswer(invocation -> {
                    TradeAccount account = invocation.getArgument(0);
                    return mapperHelper.toResponse(account);
                });
    }

    @Test
    public void shouldThrowWhenTradeAccountNotFound() {
        // Arrange
        SetExactLocationTradeAccountRequest request = createRequest();
        when(tradeAccountRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.SetExactLocation(request));
    }

    @Test
    public void shouldSetExactLocationAndSave() {
        // Arrange
        SetExactLocationTradeAccountRequest request = createRequest();

        // Act
        service.SetExactLocation(request);

        // Assert
        verify(tradeAccountRepository).save(tradeAccountCaptor.capture());
        TradeAccount actual = tradeAccountCaptor.getValue();

        assertThat(actual.getLocation().getLatitude()).isEqualTo(request.getLatitude());
        assertThat(actual.getLocation().getLongitude()).isEqualTo(request.getLongitude());
        assertThat(actual.getLocation().getTownName()).isEqualTo(request.getTownName());
        assertThat(actual.getLocation().getLocationName()).isEqualTo(request.getLocationName());
        assertThat(actual.isCoordinatesExact()).isTrue();
    }

    @Test
    public void shouldMapBeforeReturn() {
        // Arrange
        SetExactLocationTradeAccountRequest request = createRequest();
        TradeAccountResponse expected = mapperHelper.toResponse(tradeAccount);
        expected = TradeAccountResponse.builder()
                .ownerId(expected.getOwnerId())
                .name(expected.getName())
                .phone(expected.getPhone())
                .contacts(expected.getContacts())
                .description(expected.getDescription())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .townName(request.getTownName())
                .locationName(request.getLocationName())
                .coordinatesExact(true)
                .approved(false)
                .imageId(expected.getImageId())
                .build();

        // Act
        SetExactLocationTradeAccountResponse response = service.SetExactLocation(request);
        TradeAccountResponse actual = response.getTradeAccountResponse();

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    private SetExactLocationTradeAccountRequest createRequest() {
        return SetExactLocationTradeAccountRequest.builder()
                .id(UUID.randomUUID())
                .latitude(55.7558)
                .longitude(37.6173)
                .townName("Moscow")
                .locationName("Red Square, 1")
                .build();
    }

    private TradeAccount createTradeAccount() {
        User user = User.createUser(UUID.randomUUID(), "owner@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        TradeAccount tradeAccount = TradeAccount.createTradeAccount(ownerInfo);
        tradeAccount.setName("Test Account");
        tradeAccount.setPhone("+79991234567");
        return tradeAccount;
    }

}