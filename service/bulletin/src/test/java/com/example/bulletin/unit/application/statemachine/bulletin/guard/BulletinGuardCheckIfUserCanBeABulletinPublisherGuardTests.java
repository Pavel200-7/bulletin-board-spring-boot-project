package com.example.bulletin.unit.application.statemachine.bulletin.guard;

import com.example.bulletin.application.statemachine.bulletin.contract.BulletinSMHeaderContract;
import com.example.bulletin.application.statemachine.bulletin.guard.BulletinGuardsImpl;
import com.example.bulletin.domain.entity.TradeAccount;
import com.example.bulletin.domain.entity.base.Location;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import com.example.bulletin.infrastructure.repository.TradeAccountRepository;
import com.example.bulletin.infrastructure.repository.UserRepository;
import com.example.bulletin.infrastructure.security.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BulletinGuardCheckIfUserCanBeABulletinPublisherGuardTests {

    @Mock
    private SecurityService securityService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TradeAccountRepository tradeAccountRepository;

    @Mock
    private StateContext<BulletinState, BulletinEvent> context;

    @Mock
    private ExtendedState extendedState;

    @InjectMocks
    private BulletinGuardsImpl bulletinGuards;

    private Map<Object, Object> variables;
    private User user;
    private TradeAccount tradeAccount;

    @BeforeEach
    void setup() {
        variables = new HashMap<>();
        extendedState.getVariables().put(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER,
                new ArrayList<String>());
        user = User.createUser(UUID.randomUUID(), "test@example.com");

        when(context.getExtendedState())
                .thenReturn(extendedState);
        when(extendedState.getVariables())
                .thenReturn(variables);

        when(securityService.getCurrentUserIdAsUUID())
                .thenReturn(user.getId());
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        tradeAccount = createApprovedTradeAccount(user);
        when(tradeAccountRepository.findByOwnerInfo_Owner_Id(user.getId()))
                .thenReturn(Optional.of(tradeAccount));
    }

    @Test
    public void shouldApproveWhenEverythingRight() {
        // Act
        boolean result = bulletinGuards.checkIfUserCanBeABulletinPublisherGuard().evaluate(context);

        // Assert
        assertTrue(result);
    }

    @Test
    public void shouldForbidWhenUserIsNotFound() {
        // Arrange
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());

        // Act
        boolean result = bulletinGuards.checkIfUserCanBeABulletinPublisherGuard().evaluate(context);

        // Assert
        assertFalse(result);

        List<String> errors = ((List<String>) variables
                .get(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER));
        assertFalse(errors.isEmpty());
        assertEquals(errors.getFirst(), "User with this id is not found.");
    }

    @Test
    public void shouldForbidWhenUserIsBlocked() {
        // Arrange
        user.setBlocked(true);

        // Act
        boolean result = bulletinGuards.checkIfUserCanBeABulletinPublisherGuard().evaluate(context);

        // Assert
        assertFalse(result);

        List<String> errors = ((List<String>) variables
                .get(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER));
        assertFalse(errors.isEmpty());
        assertEquals(errors.getFirst(), "User with this id is blocked.");
    }

    @Test
    public void shouldForbidWhenUserDoesNotHaveTradeAccount() {
        // Arrange
        when(tradeAccountRepository.findByOwnerInfo_Owner_Id(user.getId()))
                .thenReturn(Optional.empty());
        // Act
        boolean result = bulletinGuards.checkIfUserCanBeABulletinPublisherGuard().evaluate(context);

        // Assert
        assertFalse(result);

        List<String> errors = ((List<String>) variables
                .get(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER));
        assertFalse(errors.isEmpty());
        assertEquals(errors.getFirst(), "User with this id does not have trade account.");

    }

    @Test
    public void shouldForbidWhenTradeAccountIsNotApproved() {
        // Arrange
        tradeAccount = createBlankTradeAccount(user);
        when(tradeAccountRepository.findByOwnerInfo_Owner_Id(user.getId()))
                .thenReturn(Optional.of(tradeAccount));

        // Act
        boolean result = bulletinGuards.checkIfUserCanBeABulletinPublisherGuard().evaluate(context);

        // Assert
        assertFalse(result);

        List<String> errors = ((List<String>) variables
                .get(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER));
        assertFalse(errors.isEmpty());
        assertEquals(errors.getFirst(), "Trade account of this user is not approved.");

    }


    private TradeAccount createApprovedTradeAccount(User user) {
        TradeAccount tradeAccount = createBlankTradeAccount(user);
        tradeAccount.setName("Test Account");
        tradeAccount.setPhone("+7 (999) 123-45-67");
        tradeAccount.setImageId(UUID.randomUUID());
        tradeAccount.setExactLocation(new Location(55.7558, 37.6173, "Moscow", ""));
        tradeAccount.approve();
        return tradeAccount;
    }

    private TradeAccount createBlankTradeAccount(User user) {
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return TradeAccount.createTradeAccount(ownerInfo);
    }

}
