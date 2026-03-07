package com.example.bulletin.unit.application.statemachine.bulletin.guard.helper.validationcontext;

import com.example.bulletin.application.statemachine.bulletin.contract.BulletinSMHeaderContract;
import com.example.bulletin.application.statemachine.bulletin.guard.helper.BulletinValidationContext;
import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.Errors;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BulletinValidationContextAcceptTests {

    @Mock
    private StateContext<BulletinState, BulletinEvent> context;
    @Mock
    private ExtendedState extendedState;

    private Map<Object, Object> variables;

    @BeforeEach
    void setup() {
        variables = new HashMap<>();
        when(context.getExtendedState()).thenReturn(extendedState);
        when(extendedState.getVariables()).thenReturn(variables);
    }

    @Test
    public void shouldSaveEmptyErrorsAndReturnTrue() {
        // Arrange
        BulletinValidationContext validationContext = new BulletinValidationContext(context);

        // Act
        boolean result = validationContext.accept();

        // Assert
        assertTrue(result);

        Errors savedErrors = (Errors) variables
                .get(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER);
        assertNotNull(savedErrors);
        assertFalse(savedErrors.hasErrors());
    }

    @Test
    public void shouldOverwritePreviousErrors() {
        // Arrange
        BulletinValidationContext validationContext = new BulletinValidationContext(context);
        validationContext.addFieldError("title", "Title is required");
        validationContext.reject();

        // Act
        boolean result = validationContext.accept();

        // Assert
        assertTrue(result);

        Errors savedErrors = (Errors) variables
                .get(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER);
        assertNotNull(savedErrors);
        assertFalse(savedErrors.hasErrors());
    }

}