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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class BulletinValidationContextHasErrorsTests {

    @Mock
    private StateContext<BulletinState, BulletinEvent> context;

    @Test
    public void shouldReturnFalseWhenNoErrors() {
        // Arrange
        BulletinValidationContext validationContext = new BulletinValidationContext(context);

        // Act & Assert
        assertFalse(validationContext.hasErrors());
    }

    @Test
    public void shouldReturnTrueWhenFieldErrorExists() {
        // Arrange
        BulletinValidationContext validationContext = new BulletinValidationContext(context);
        validationContext.addFieldError("title", "Title is required");

        // Act & Assert
        assertTrue(validationContext.hasErrors());
    }

    @Test
    public void shouldReturnTrueWhenObjectErrorExists() {
        // Arrange
        BulletinValidationContext validationContext = new BulletinValidationContext(context);
        validationContext.addObjectError("Global error");

        // Act & Assert
        assertTrue(validationContext.hasErrors());
    }

}