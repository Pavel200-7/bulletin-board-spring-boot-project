package com.example.bulletin.unit.application.statemachine.bulletin.guard;

import com.example.bulletin.application.statemachine.bulletin.contract.BulletinExtendedState;
import com.example.bulletin.application.statemachine.bulletin.guard.BulletinGuardsImpl;
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
public class BulletinGuardCheckIfUserIsAdminGuardTests {

    @Mock
    private SecurityService securityService;

    @Mock
    private StateContext<BulletinState, BulletinEvent> context;

    @Mock
    private ExtendedState extendedState;

    @InjectMocks
    private BulletinGuardsImpl bulletinGuards;

    private Map<Object, Object> variables;

    @BeforeEach
    void setUp() {
        variables = new HashMap<>();
        when(context.getExtendedState()).thenReturn(extendedState);
        when(extendedState.getVariables()).thenReturn(variables);
    }

    @Test
    public void shouldApproveWhenUserIsAdmin() {
        // Arrange
        when(securityService.isAdmin()).thenReturn(true);

        // Act
        boolean result = bulletinGuards.checkIfUserIsAdminGuard().evaluate(context);

        // Assert
        assertTrue(result);

        Errors errors = (Errors) variables
                .get(BulletinExtendedState.BULLETIN_VALIDATION_RESULT);
        assertNotNull(errors);
        assertFalse(errors.hasErrors());

        verify(securityService, times(1)).isAdmin();
    }

    @Test
    public void shouldForbidWhenUserIsNotAdmin() {
        // Arrange
        when(securityService.isAdmin()).thenReturn(false);

        // Act
        boolean result = bulletinGuards.checkIfUserIsAdminGuard().evaluate(context);

        // Assert
        assertFalse(result);

        Errors errors = (Errors) variables
                .get(BulletinExtendedState.BULLETIN_VALIDATION_RESULT);
        assertNotNull(errors);
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());

        ObjectError globalError = errors.getGlobalError();
        assertNotNull(globalError);
        assertEquals("Only administrators can perform this action", globalError.getDefaultMessage());

        verify(securityService, times(1)).isAdmin();
    }

    @Test
    public void shouldClearPreviousErrorsOnSuccess() {
        // Arrange
        Errors previousErrors = mock(Errors.class);
        variables.put(BulletinExtendedState.BULLETIN_VALIDATION_RESULT, previousErrors);

        when(securityService.isAdmin()).thenReturn(true);

        // Act
        boolean result = bulletinGuards.checkIfUserIsAdminGuard().evaluate(context);

        // Assert
        assertTrue(result);

        Errors errors = (Errors) variables
                .get(BulletinExtendedState.BULLETIN_VALIDATION_RESULT);
        assertNotNull(errors);
        assertFalse(errors.hasErrors());
        assertNotEquals(previousErrors, errors);
    }

    @Test
    public void shouldOverwriteErrorsOnFailure() {
        // Arrange
        Errors previousErrors = mock(Errors.class);
        variables.put(BulletinExtendedState.BULLETIN_VALIDATION_RESULT, previousErrors);

        when(securityService.isAdmin()).thenReturn(false);

        // Act
        boolean result = bulletinGuards.checkIfUserIsAdminGuard().evaluate(context);

        // Assert
        assertFalse(result);

        Errors errors = (Errors) variables
                .get(BulletinExtendedState.BULLETIN_VALIDATION_RESULT);
        assertNotNull(errors);
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());

        ObjectError globalError = errors.getGlobalError();
        assertNotNull(globalError);
        assertEquals("Only administrators can perform this action", globalError.getDefaultMessage());
        assertNotEquals(previousErrors, errors);
    }

    private String getObjectErrorMes(Errors errors) {
        return errors.getGlobalErrors()
                .getFirst()
                .getDefaultMessage();
    }

}