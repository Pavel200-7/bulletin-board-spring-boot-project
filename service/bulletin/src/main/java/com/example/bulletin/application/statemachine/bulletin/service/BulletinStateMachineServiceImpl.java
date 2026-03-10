package com.example.bulletin.application.statemachine.bulletin.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.statemachine.bulletin.BulletinStateChangeListener;
import com.example.bulletin.application.statemachine.bulletin.contract.BulletinExtendedState;
import com.example.bulletin.application.statemachine.bulletin.contract.BulletinMessageState;
import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import com.example.bulletin.infrastructure.repository.BulletinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class BulletinStateMachineServiceImpl implements BulletinStateMachineService {

    private final StateMachineFactory<BulletinState, BulletinEvent> factory;
    private final BulletinRepository bulletinRepository;
    private final BulletinStateChangeListener stateChangeListener;

    @Override
    public void sendEvent(Message<BulletinEvent> message)
            throws Exception {
        UUID bulletinId = message.getHeaders()
                .get(BulletinMessageState.BULLETIN_ID, UUID.class);
        StateMachine<BulletinState, BulletinEvent> machine = restore(bulletinId);
        machine.sendEvent(message);

        log.info("Итоговое состояние bulletin: {}", getBulletin(machine).getState().name());

        Optional<Exception> exception = getException(machine);
        if (exception.isPresent()) {
            throw exception.get();
        }

        BindingResult validationErrors = getValidationErrors(machine);
        if (validationErrors.hasErrors()) {
            log.info("Ошибки валидации.");
            throw new BindException(validationErrors);
        }
    }

    @Override
    public StateMachine<BulletinState, BulletinEvent> restore(UUID bulletinId) {
        Bulletin bulletin = bulletinRepository.findByIdEager(bulletinId)
                .orElseThrow(() -> new ResourceNotFoundException("Bulletin not found."));

        StateMachine<BulletinState, BulletinEvent> machine =
                factory.getStateMachine(bulletinId.toString());

        machine.stop();

        machine.getStateMachineAccessor().doWithAllRegions(access -> {
            access.addStateMachineInterceptor(stateChangeListener);

            StateMachineContext<BulletinState, BulletinEvent> context =
                    new DefaultStateMachineContext<>(
                            bulletin.getState(),
                            null,
                            null,
                            null,
                            null
                    );
            access.resetStateMachine(context);
        });

        machine.start();

        log.info("После start() - запущена, состояние: {}", machine.getState());
        setExtendedState(machine, bulletin);
        return machine;
    }

    private void setExtendedState(StateMachine<BulletinState, BulletinEvent> machine,
                                                 Bulletin bulletin) {
        machine.getExtendedState().getVariables()
                .put(BulletinExtendedState.BULLETIN, bulletin);
        machine.getExtendedState().getVariables()
                .put(BulletinExtendedState.BULLETIN_ID, bulletin.getId());

        Errors emptyErrorsHoled = new BeanPropertyBindingResult(Bulletin.class, "bulletin");
        machine.getExtendedState().getVariables()
                .put(BulletinExtendedState.BULLETIN_VALIDATION_RESULT, emptyErrorsHoled);
    }

    private BindingResult getValidationErrors(StateMachine<BulletinState, BulletinEvent> machine) {
        return (BindingResult) machine.getExtendedState()
                .getVariables()
                .get(BulletinExtendedState.BULLETIN_VALIDATION_RESULT);
    }

    private Bulletin getBulletin(StateMachine<BulletinState, BulletinEvent> machine) {
        return machine.getExtendedState()
                .get(BulletinExtendedState.BULLETIN, Bulletin.class);
    }

    private Optional<Exception> getException(StateMachine<BulletinState, BulletinEvent> machine) {
        return Optional.ofNullable((Exception) machine.getExtendedState()
                .getVariables()
                .get(BulletinExtendedState.EXCEPTION));
    }

}
