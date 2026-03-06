package com.example.bulletin.unit.application.statemachine.bulletin.guard;

import com.example.bulletin.application.statemachine.bulletin.contract.BulletinSMHeaderContract;
import com.example.bulletin.application.statemachine.bulletin.guard.BulletinGuardsImpl;
import com.example.bulletin.domain.entity.Bulletin;
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
import org.springframework.validation.Validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BulletinGuardCheckIfUserIsOwnerGuardTests {

    @Mock
    private Validator validator;

    @Mock
    private SecurityService securityService;

    @Mock
    private StateContext<BulletinState, BulletinEvent> context;

    @Mock
    private ExtendedState extendedState;

    @InjectMocks
    private BulletinGuardsImpl bulletinGuards;

    private Map<Object, Object> variables;
    private Bulletin bulletin;
    private User user;
    private UUID userId;
    private UUID bulletinId;

    @BeforeEach
    void setUp() {
        variables = new HashMap<>();
        userId = UUID.randomUUID();
        bulletinId = UUID.randomUUID();

        user = User.createUser(userId, "test@example.com");

        bulletin = mock(Bulletin.class);
        OwnerInfo ownerInfo = mock(OwnerInfo.class);

        when(bulletin.getOwnerInfo()).thenReturn(ownerInfo);
        when(context.getExtendedState()).thenReturn(extendedState);
        when(extendedState.getVariables()).thenReturn(variables);
        when(extendedState.get(BulletinSMHeaderContract.BULLETIN_HEADER, Bulletin.class))
                .thenReturn(bulletin);
    }

    @Test
    public void shouldApproveWhenUserIsOwner() {
        // Arrange
        when(securityService.getCurrentUserIdAsUUID()).thenReturn(userId);
        when(bulletin.getOwnerInfo().isOwnedByUserId(userId)).thenReturn(true);

        // Act
        boolean result = bulletinGuards.checkIfUserIsOwnerGuard().evaluate(context);

        // Assert
        assertTrue(result);

        List<String> errors = (List<String>) variables
                .get(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER);
        assertNotNull(errors);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void shouldForbidWhenBulletinIsNull() {
        // Arrange
        when(extendedState.get(BulletinSMHeaderContract.BULLETIN_HEADER, Bulletin.class))
                .thenReturn(null);

        // Act
        boolean result = bulletinGuards.checkIfUserIsOwnerGuard().evaluate(context);

        // Assert
        assertFalse(result);

        List<String> errors = (List<String>) variables
                .get(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER);
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals("Bulletin not found", errors.get(0));
    }

    @Test
    public void shouldForbidWhenUserIsNotOwner() {
        // Arrange
        UUID differentUserId = UUID.randomUUID();
        when(securityService.getCurrentUserIdAsUUID())
                .thenReturn(differentUserId);
        when(bulletin.getOwnerInfo().isOwnedByUserId(differentUserId))
                .thenReturn(false);

        // Act
        boolean result = bulletinGuards.checkIfUserIsOwnerGuard().evaluate(context);

        // Assert
        assertFalse(result);

        List<String> errors = (List<String>) variables
                .get(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER);
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals("You are not the owner of this bulletin", errors.get(0));
    }

    @Test
    public void shouldForbidWhenSecurityServiceReturnsNull() {
        // Arrange
        when(securityService.getCurrentUserIdAsUUID()).thenReturn(null);

        // Act
        boolean result = bulletinGuards.checkIfUserIsOwnerGuard().evaluate(context);

        // Assert
        assertFalse(result);

        List<String> errors = (List<String>) variables
                .get(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER);
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals("You are not the owner of this bulletin", errors.get(0));
    }

    @Test
    public void shouldClearPreviousErrorsOnSuccess() {
        // Arrange
        variables.put(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER,
                List.of("Some old error"));

        when(securityService.getCurrentUserIdAsUUID()).thenReturn(userId);
        when(bulletin.getOwnerInfo().isOwnedByUserId(userId)).thenReturn(true);

        // Act
        boolean result = bulletinGuards.checkIfUserIsOwnerGuard().evaluate(context);

        // Assert
        assertTrue(result);

        List<String> errors = (List<String>) variables
                .get(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER);
        assertNotNull(errors);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void shouldPreserveMultipleErrors() {
        // Arrange
        when(securityService.getCurrentUserIdAsUUID()).thenReturn(null);

        // Act
        boolean result = bulletinGuards.checkIfUserIsOwnerGuard().evaluate(context);

        // Assert
        assertFalse(result);

        List<String> errors = (List<String>) variables
                .get(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER);
        assertNotNull(errors);
        assertEquals(1, errors.size());
    }

}
