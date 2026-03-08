package com.example.bulletin.unit.application.statemachine.bulletin.guard.helper.validationcontext;

import com.example.bulletin.application.statemachine.bulletin.contract.BulletinExtendedState;
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
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BulletinValidationContextRejectTests {

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
    public void shouldSaveErrorsAndReturnFalse() {
        // Arrange
        BulletinValidationContext validationContext = new BulletinValidationContext(context);
        validationContext.addFieldError("title", "Title is required");

        // Act
        boolean result = validationContext.reject();

        // Assert
        assertFalse(result);

        Errors savedErrors = (Errors) variables
                .get(BulletinExtendedState.BULLETIN_VALIDATION_RESULT);
        assertNotNull(savedErrors);
        assertTrue(savedErrors.hasErrors());
        assertEquals(1, savedErrors.getErrorCount());

        FieldError fieldError = savedErrors.getFieldError("title");
        assertNotNull(fieldError);
        assertEquals("Title is required", fieldError.getDefaultMessage());
    }

    @Test
    public void shouldSaveEmptyErrorsWhenNoErrors() {
        // Arrange
        BulletinValidationContext validationContext = new BulletinValidationContext(context);

        // Act
        boolean result = validationContext.reject();

        // Assert
        assertFalse(result);

        Errors savedErrors = (Errors) variables
                .get(BulletinExtendedState.BULLETIN_VALIDATION_RESULT);
        assertNotNull(savedErrors);
        assertFalse(savedErrors.hasErrors());
    }

}