package com.example.bulletin.unit.application.statemachine.bulletin.guard;

import com.example.bulletin.application.statemachine.bulletin.contract.BulletinSMHeaderContract;
import com.example.bulletin.application.statemachine.bulletin.guard.BulletinGuardsImpl;
import com.example.bulletin.application.statemachine.bulletin.guard.helper.validationdto.BulletinApproveValidationDto;
import com.example.bulletin.domain.entity.*;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class BulletinGuardsCheckIfCanBeApprovedGuardTests {

    @Mock
    private Validator validator;

    @Mock
    private StateContext<BulletinState, BulletinEvent> context;

    @Mock
    private ExtendedState extendedState;

    @InjectMocks
    private BulletinGuardsImpl bulletinGuards;

    private Map<Object, Object> variables;
    private Bulletin bulletin;

    @BeforeEach
    void setup() {
        variables = new HashMap<>();

        User user = User.createUser(UUID.randomUUID(), "test@example.com");
        OwnerInfo ownerInfo = new OwnerInfo(user);
        bulletin = Bulletin.createDraft(ownerInfo);

        when(context.getExtendedState())
                .thenReturn(extendedState);
        when(extendedState.getVariables())
                .thenReturn(variables);
    }

    @Test
    public void shouldReturnTrueWhenValidationPasses() {
        // Arrange
        setupValidBulletin();
        when(extendedState.get(BulletinSMHeaderContract.BULLETIN_HEADER, Bulletin.class))
                .thenReturn(bulletin);
        doAnswer(answer -> null)
                .when(validator).validate(any(BulletinApproveValidationDto.class), any(Errors.class));

        // Act
        boolean result = bulletinGuards.checkIfCanBeApprovedGuard().evaluate(context);

        // Assert
        assertTrue(result);
        verify(validator).validate(any(BulletinApproveValidationDto.class), any(Errors.class));

        Errors errors = (Errors) variables
                .get(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER);
        assertNotNull(errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void shouldReturnFalseWhenBulletinIsNull() {
        // Arrange
        when(extendedState.get(BulletinSMHeaderContract.BULLETIN_HEADER, Bulletin.class))
                .thenReturn(null);

        // Act
        boolean result = bulletinGuards.checkIfCanBeApprovedGuard().evaluate(context);

        // Assert
        assertFalse(result);
        verify(validator, never()).validate(any(), any(Errors.class));

        Errors errors = (Errors) variables
                .get(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER);
        assertNotNull(errors);
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());

        ObjectError globalError = errors.getGlobalError();
        assertNotNull(globalError);
        assertEquals("Bulletin not found", globalError.getDefaultMessage());
    }

    @Test
    public void shouldReturnFalseWhenValidationFails() {
        // Arrange
        setupValidBulletin();
        when(extendedState.get(BulletinSMHeaderContract.BULLETIN_HEADER, Bulletin.class))
                .thenReturn(bulletin);

        doAnswer(invocation -> {
            Errors errors = invocation.getArgument(1);
            errors.rejectValue("title", "error", "Title is required");
            errors.rejectValue("description", "error", "Description is required");
            errors.rejectValue("categoryId", "error", "Category is required");
            return null;
        }).when(validator).validate(any(BulletinApproveValidationDto.class), any(Errors.class));

        // Act
        boolean result = bulletinGuards.checkIfCanBeApprovedGuard().evaluate(context);

        // Assert
        assertFalse(result);
        verify(validator).validate(any(BulletinApproveValidationDto.class), any(Errors.class));

        Errors errors = (Errors) variables
                .get(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER);
        assertNotNull(errors);
        assertTrue(errors.hasErrors());
        assertEquals(3, errors.getErrorCount());
        assertEquals(3, errors.getFieldErrorCount());
    }

    @Test
    public void shouldPreserveErrorOrder() {
        // Arrange
        setupValidBulletin();
        when(extendedState.get(BulletinSMHeaderContract.BULLETIN_HEADER, Bulletin.class))
                .thenReturn(bulletin);

        List<String> expectedErrors = List.of(
                "Title is required",
                "Description is required"
        );

        doAnswer(invocation -> {
            Errors errors = invocation.getArgument(1);
            expectedErrors.forEach(error ->
                    errors.rejectValue("title", "error", error)
            );
            return null;
        }).when(validator).validate(any(BulletinApproveValidationDto.class), any(Errors.class));

        // Act
        boolean result = bulletinGuards.checkIfCanBeApprovedGuard().evaluate(context);

        // Assert
        assertFalse(result);

        Errors errors = (Errors) variables
                .get(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER);
        assertNotNull(errors);
        assertEquals(2, errors.getErrorCount());

        List<FieldError> fieldErrors = errors.getFieldErrors();
        assertEquals(2, fieldErrors.size());
        assertEquals(expectedErrors.get(0), fieldErrors.get(0).getDefaultMessage());
        assertEquals(expectedErrors.get(1), fieldErrors.get(1).getDefaultMessage());
    }

    @Test
    public void shouldOverridePreviousValidationResult() {
        // Arrange
        setupValidBulletin();
        when(extendedState.get(BulletinSMHeaderContract.BULLETIN_HEADER, Bulletin.class))
                .thenReturn(bulletin);

        Errors previousErrors = mock(Errors.class);
        variables.put(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER, previousErrors);

        doAnswer(invocation -> null)
                .when(validator).validate(any(BulletinApproveValidationDto.class), any(Errors.class));

        // Act
        boolean result = bulletinGuards.checkIfCanBeApprovedGuard().evaluate(context);

        // Assert
        assertTrue(result);

        Errors errors = (Errors) variables
                .get(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER);
        assertNotNull(errors);
        assertFalse(errors.hasErrors());
        assertNotEquals(previousErrors, errors);
    }

    private void setupValidBulletin() {
        bulletin.setTitle("Valid Title");
        bulletin.setDescription("Valid description that is long enough");

        Category root = Category.createRoot("root");
        Category leafCategory = root.createLeafyChild("leaf");
        bulletin.setCategory(leafCategory);

        Characteristic characteristic = leafCategory.addCharacteristic("color");
        BulletinCharacteristic bc = bulletin.addCharacteristic(characteristic);
        CharacteristicValue value = characteristic.addPossibleValue("red");
        bc.setValue(value);

        bulletin.addImage(UUID.randomUUID());
    }

    private String getObjectErrorMes(Errors errors) {
        return errors.getGlobalErrors()
                .getFirst()
                .getDefaultMessage();
    }

}