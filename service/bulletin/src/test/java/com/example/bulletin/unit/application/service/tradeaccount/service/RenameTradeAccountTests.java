package com.example.bulletin.unit.application.service.tradeaccount.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.TradeAccountMapper;
import com.example.bulletin.application.service.tradeaccount.TradeAccountServiceImpl;
import com.example.bulletin.application.service.tradeaccount.data.request.RenameTradeAccountRequest;
import com.example.bulletin.application.service.tradeaccount.data.response.RenameTradeAccountResponse;
import com.example.bulletin.application.data.response.TradeAccountResponse;
import com.example.bulletin.domain.entity.TradeAccount;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.infrastructure.repository.TradeAccountRepository;
import com.example.bulletin.infrastructure.security.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
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
public class RenameTradeAccountTests {

    private TradeAccountMapper mapperHelper = Mappers.getMapper(
            TradeAccountMapper.class);

    @Mock
    private TradeAccountRepository tradeAccountRepository;

    @Mock
    private TradeAccountMapper mapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private TradeAccountServiceImpl service;

    @Captor
    private ArgumentCaptor<TradeAccount> tradeAccountCaptor;

    private TradeAccount tradeAccount;

    @BeforeEach
    public void setup() {
        tradeAccount = createTradeAccount();

        when(securityService.getCurrentUserIdAsUUID())
                .thenReturn(UUID.randomUUID());

        when(tradeAccountRepository.findByOwnerInfo_Owner_Id(any(UUID.class)))
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
        RenameTradeAccountRequest request = createRequest();
        when(tradeAccountRepository.findByOwnerInfo_Owner_Id(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.renameTradeAccount(request));
    }

    @Test
    public void shouldRenameTradeAccountAndSave() {
        // Arrange
        RenameTradeAccountRequest request = createRequest();

        // Act
        service.renameTradeAccount(request);

        // Assert
        verify(tradeAccountRepository).save(tradeAccountCaptor.capture());
        TradeAccount actual = tradeAccountCaptor.getValue();

        assertThat(actual.getName()).isEqualTo(request.getName());
    }

    @Test
    public void shouldMapBeforeReturn() {
        // Arrange
        RenameTradeAccountRequest request = createRequest();
        TradeAccountResponse expected = mapperHelper.toResponse(tradeAccount);
        expected = TradeAccountResponse.builder()
                .ownerId(expected.getOwnerId())
                .name(request.getName())
                .phone(expected.getPhone())
                .contacts(expected.getContacts())
                .description(expected.getDescription())
                .latitude(expected.getLatitude())
                .longitude(expected.getLongitude())
                .locationName(expected.getLocationName())
                .coordinatesExact(expected.isCoordinatesExact())
                .approved(expected.isApproved())
                .imageId(expected.getImageId())
                .build();

        // Act
        RenameTradeAccountResponse response = service.renameTradeAccount(request);
        TradeAccountResponse actual = response.getTradeAccountResponse();

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    private RenameTradeAccountRequest createRequest() {
        return RenameTradeAccountRequest.builder()
                .name("New Account Name")
                .build();
    }

    private TradeAccount createTradeAccount() {
        User user = User.createUser(UUID.randomUUID(), "owner@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        TradeAccount tradeAccount = TradeAccount.createTradeAccount(ownerInfo);
        tradeAccount.setName("Old Name");
        tradeAccount.setPhone("+79991234567");
        return tradeAccount;
    }

}