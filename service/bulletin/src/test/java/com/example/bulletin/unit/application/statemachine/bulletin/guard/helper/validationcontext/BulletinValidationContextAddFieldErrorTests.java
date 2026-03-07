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

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BulletinValidationContextAddFieldErrorTests {

    @Mock
    private StateContext<BulletinState, BulletinEvent> context;
    @Mock
    private ExtendedState extendedState;

    private Map<Object, Object> variables;

    @BeforeEach
    void setup() {
        variables = new HashMap<>();
        extendedState.getVariables().put(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER,
                new ArrayList<String>());

        when(context.getExtendedState())
                .thenReturn(extendedState);
        when(extendedState.getVariables())
                .thenReturn(variables);
    }

    @Test
    public void shouldAddFieldError() {
        // Arrange
        BulletinValidationContext validationContext = new BulletinValidationContext(context);

        // Act
        validationContext.addFieldError("field", "should be set");

        // Assert
        assertTrue(validationContext.hasErrors());
        assertEquals(getFieldErrorMes(validationContext.getErrors()),
                "should be set");
    }

    @Test
    public void shouldAddMultipleFieldErrors() {
        // Arrange
        BulletinValidationContext validationContext = new BulletinValidationContext(context);

        // Act
        validationContext
                .addFieldError("title", "Title is required")
                .addFieldError("description", "Description is required");

        // Assert
        assertTrue(validationContext.hasErrors());
        Errors errors = validationContext.getErrors();
        assertEquals(2, errors.getErrorCount());
        assertEquals(2, errors.getFieldErrorCount());

        assertEquals("Title is required", errors.getFieldError("title").getDefaultMessage());
        assertEquals("Description is required", errors.getFieldError("description").getDefaultMessage());
    }

    private String getFieldErrorMes(Errors errors) {
        return errors.getFieldErrors()
                .getFirst()
                .getDefaultMessage();
    }

}