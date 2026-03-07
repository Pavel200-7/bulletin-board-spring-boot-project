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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class BulletinValidationContextGetErrorsTests {

    @Mock
    private StateContext<BulletinState, BulletinEvent> context;

    @Test
    public void shouldReturnErrorsObject() {
        // Arrange
        BulletinValidationContext validationContext = new BulletinValidationContext(context);

        // Act
        Errors errors = validationContext.getErrors();

        // Assert
        assertNotNull(errors);
        assertSame(validationContext.getErrors(), errors);
    }

}