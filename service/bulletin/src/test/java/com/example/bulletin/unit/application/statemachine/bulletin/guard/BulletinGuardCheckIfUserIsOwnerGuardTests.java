package com.example.bulletin.unit.application.statemachine.bulletin.guard;

import com.example.bulletin.application.statemachine.bulletin.contract.BulletinExtendedState;
import com.example.bulletin.application.statemachine.bulletin.guard.BulletinGuardsImpl;
import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
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
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BulletinGuardCheckIfUserIsOwnerGuardTests {

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
        when(extendedState.get(BulletinExtendedState.BULLETIN, Bulletin.class))
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

        Errors errors = (Errors) variables
                .get(BulletinExtendedState.BULLETIN_VALIDATION_RESULT);
        assertNotNull(errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void shouldForbidWhenBulletinIsNull() {
        // Arrange
        when(extendedState.get(BulletinExtendedState.BULLETIN, Bulletin.class))
                .thenReturn(null);

        // Act
        boolean result = bulletinGuards.checkIfUserIsOwnerGuard().evaluate(context);

        // Assert
        assertFalse(result);

        Errors errors = (Errors) variables
                .get(BulletinExtendedState.BULLETIN_VALIDATION_RESULT);
        assertNotNull(errors);
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());

        ObjectError globalError = errors.getGlobalError();
        assertNotNull(globalError);
        assertEquals("Bulletin not found", globalError.getDefaultMessage());
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

        Errors errors = (Errors) variables
                .get(BulletinExtendedState.BULLETIN_VALIDATION_RESULT);
        assertNotNull(errors);
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());

        ObjectError globalError = errors.getGlobalError();
        assertNotNull(globalError);
        assertEquals("You are not the owner of this bulletin", globalError.getDefaultMessage());
    }

    @Test
    public void shouldForbidWhenSecurityServiceReturnsNull() {
        // Arrange
        when(securityService.getCurrentUserIdAsUUID()).thenReturn(null);

        // Act
        boolean result = bulletinGuards.checkIfUserIsOwnerGuard().evaluate(context);

        // Assert
        assertFalse(result);

        Errors errors = (Errors) variables
                .get(BulletinExtendedState.BULLETIN_VALIDATION_RESULT);
        assertNotNull(errors);
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());

        ObjectError globalError = errors.getGlobalError();
        assertNotNull(globalError);
        assertEquals("You are not the owner of this bulletin", globalError.getDefaultMessage());
    }

    @Test
    public void shouldClearPreviousErrorsOnSuccess() {
        // Arrange
        Errors previousErrors = mock(Errors.class);
        variables.put(BulletinExtendedState.BULLETIN_VALIDATION_RESULT, previousErrors);

        when(securityService.getCurrentUserIdAsUUID()).thenReturn(userId);
        when(bulletin.getOwnerInfo().isOwnedByUserId(userId)).thenReturn(true);

        // Act
        boolean result = bulletinGuards.checkIfUserIsOwnerGuard().evaluate(context);

        // Assert
        assertTrue(result);

        Errors errors = (Errors) variables
                .get(BulletinExtendedState.BULLETIN_VALIDATION_RESULT);
        assertNotNull(errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void shouldPreserveMultipleErrors() {
        // Arrange
        when(securityService.getCurrentUserIdAsUUID()).thenReturn(null);

        // Act
        boolean result = bulletinGuards.checkIfUserIsOwnerGuard().evaluate(context);

        // Assert
        assertFalse(result);

        Errors errors = (Errors) variables
                .get(BulletinExtendedState.BULLETIN_VALIDATION_RESULT);
        assertNotNull(errors);
        assertEquals(1, errors.getErrorCount());
    }

    private String getObjectErrorMes(Errors errors) {
        return errors.getGlobalErrors()
                .getFirst()
                .getDefaultMessage();
    }
}