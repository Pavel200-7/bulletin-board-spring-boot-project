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
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class BulletinValidationContextAddErrorsTests {

    @Mock
    private StateContext<BulletinState, BulletinEvent> context;

    @Test
    public void shouldAddErrorsFromOtherErrors() {
        // Arrange
        BulletinValidationContext validationContext = new BulletinValidationContext(context);

        BulletinValidationContext anotherValidationContext = new BulletinValidationContext(context);
        anotherValidationContext.addFieldError("title", "Title is required");
        anotherValidationContext.addFieldError("description", "Description is required");
        anotherValidationContext.addObjectError( "Global error");
        Errors anotherErrors = anotherValidationContext.getErrors();


        // Act
        validationContext.addErrors(anotherErrors);

        // Assert
        assertTrue(validationContext.hasErrors());
        Errors errors = validationContext.getErrors();
        assertEquals(3, errors.getErrorCount());
        assertEquals(2, errors.getFieldErrorCount());
        assertEquals(1, errors.getGlobalErrorCount());

        assertEquals("Title is required", errors.getFieldError("title").getDefaultMessage());
        assertEquals("Description is required", errors.getFieldError("description").getDefaultMessage());
        assertEquals("Global error", errors.getGlobalError().getDefaultMessage());
    }

    @Test
    public void shouldHandleEmptyErrors() {
        // Arrange
        BulletinValidationContext validationContext = new BulletinValidationContext(context);
        Errors emptyErrors = new BeanPropertyBindingResult(new Object(), "empty");

        // Act
        validationContext.addErrors(emptyErrors);

        // Assert
        assertFalse(validationContext.hasErrors());
    }

}