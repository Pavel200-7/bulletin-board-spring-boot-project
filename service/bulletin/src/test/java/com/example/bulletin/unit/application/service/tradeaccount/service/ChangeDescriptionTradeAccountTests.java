package com.example.bulletin.unit.application.service.tradeaccount.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.TradeAccountMapper;
import com.example.bulletin.application.service.tradeaccount.TradeAccountServiceImpl;
import com.example.bulletin.application.service.tradeaccount.data.request.ChangeDescriptionTradeAccountRequest;
import com.example.bulletin.application.service.tradeaccount.data.response.ChangeDescriptionTradeAccountResponse;
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
public class ChangeDescriptionTradeAccountTests {

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
        ChangeDescriptionTradeAccountRequest request = createRequest();
        when(tradeAccountRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.changeDescription(request));
    }

    @Test
    public void shouldChangeDescriptionAndSave() {
        // Arrange
        ChangeDescriptionTradeAccountRequest request = createRequest();

        // Act
        service.changeDescription(request);

        // Assert
        verify(tradeAccountRepository).save(tradeAccountCaptor.capture());
        TradeAccount actual = tradeAccountCaptor.getValue();

        assertThat(actual.getDescription()).isEqualTo(request.getDescription());
    }

    @Test
    public void shouldMapBeforeReturn() {
        // Arrange
        ChangeDescriptionTradeAccountRequest request = createRequest();
        TradeAccountResponse expected = mapperHelper.toResponse(tradeAccount);
        expected = TradeAccountResponse.builder()
                .ownerId(expected.getOwnerId())
                .name(expected.getName())
                .phone(expected.getPhone())
                .contacts(expected.getContacts())
                .description(request.getDescription())
                .latitude(expected.getLatitude())
                .longitude(expected.getLongitude())
                .locationName(expected.getLocationName())
                .coordinatesExact(expected.isCoordinatesExact())
                .approved(expected.isApproved())
                .imageId(expected.getImageId())
                .build();

        // Act
        ChangeDescriptionTradeAccountResponse response = service.changeDescription(request);
        TradeAccountResponse actual = response.getTradeAccountResponse();

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    private ChangeDescriptionTradeAccountRequest createRequest() {
        return ChangeDescriptionTradeAccountRequest.builder()
                .id(UUID.randomUUID())
                .description("New description for trade account")
                .build();
    }

    private TradeAccount createTradeAccount() {
        User user = User.createUser(UUID.randomUUID(), "owner@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        TradeAccount tradeAccount = TradeAccount.createTradeAccount(ownerInfo);
        tradeAccount.setName("Test Account");
        tradeAccount.setPhone("+79991234567");
        tradeAccount.setDescription("Old description");
        return tradeAccount;
    }

}