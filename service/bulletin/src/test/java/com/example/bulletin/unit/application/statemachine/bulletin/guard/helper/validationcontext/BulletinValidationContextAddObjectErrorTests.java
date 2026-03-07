package com.example.bulletin.unit.application.statemachine.bulletin.guard.helper.validationcontext;

import com.example.bulletin.application.statemachine.bulletin.guard.helper.BulletinValidationContext;
import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.StateContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class BulletinValidationContextAddObjectErrorTests {

    @Mock
    private StateContext<BulletinState, BulletinEvent> context;

    @Test
    public void shouldAddObjectError() {
        // Arrange
        BulletinValidationContext validationContext = new BulletinValidationContext(context);

        // Act
        validationContext.addObjectError("Global validation error");

        // Assert
        assertTrue(validationContext.hasErrors());
        Errors errors = validationContext.getErrors();
        assertEquals(1, errors.getErrorCount());
        assertEquals(1, errors.getGlobalErrorCount());

        ObjectError globalError = errors.getGlobalError();
        assertEquals("Global validation error", globalError.getDefaultMessage());
    }

    @Test
    public void shouldAddMultipleObjectErrors() {
        // Arrange
        BulletinValidationContext validationContext = new BulletinValidationContext(context);

        // Act
        validationContext
                .addObjectError("First global error")
                .addObjectError("Second global error");

        // Assert
        assertTrue(validationContext.hasErrors());
        Errors errors = validationContext.getErrors();
        assertEquals(2, errors.getErrorCount());
        assertEquals(2, errors.getGlobalErrorCount());
    }

}