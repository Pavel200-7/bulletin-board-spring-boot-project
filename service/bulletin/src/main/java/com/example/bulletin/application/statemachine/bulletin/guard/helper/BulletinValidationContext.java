package com.example.bulletin.application.statemachine.bulletin.guard.helper;

import com.example.bulletin.application.statemachine.bulletin.contract.BulletinSMHeaderContract;
import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import org.springframework.statemachine.StateContext;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.*;

public class BulletinValidationContext {

    private final StateContext<BulletinState, BulletinEvent> context;
    private final BeanPropertyBindingResult errors;


    public BulletinValidationContext(StateContext<BulletinState, BulletinEvent> context) {
        this.context = context;
        this.errors = new BeanPropertyBindingResult(Bulletin.class, "bulletin");
    }

    public Errors getErrors() {
        return errors;
    }

    public BulletinValidationContext addFieldError(String field, String errorMes) {
        errors.addError(new FieldError(
                errors.getObjectName(),
                field,
                null,
                false,
                new String[]{"error." + field},
                null,
                errorMes
        ));
        return this;
    }

    public BulletinValidationContext addObjectError(String errorMes) {
        errors.addError(new ObjectError(
                errors.getObjectName(),
                new String[]{"error"},
                null,
                errorMes
        ));
        return this;
    }

    public void addErrors(Errors otherErrors) {
        otherErrors.getFieldErrors().forEach(fe ->
                this.addFieldError(fe.getField(), fe.getDefaultMessage())
        );
        otherErrors.getGlobalErrors().forEach(oe ->
                this.addObjectError(oe.getDefaultMessage())
        );
    }

    public boolean hasErrors() {
        return errors.hasErrors();
    }

    public boolean reject() {
        saveErrors();
        return false;
    }

    public boolean accept() {
        context.getExtendedState().getVariables()
                .put(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER,
                        new BeanPropertyBindingResult(Bulletin.class, "bulletin"));
        return true;
    }

    private void saveErrors() {
        context.getExtendedState().getVariables()
                .put(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER, errors);
    }

}
