package com.example.bulletin.application.statemachine.bulletin.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.statemachine.bulletin.BulletinStateChangeListener;
import com.example.bulletin.application.statemachine.bulletin.contract.BulletinSMHeaderContract;
import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import com.example.bulletin.infrastructure.repository.BulletinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.statemachine.StateMachine;
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
    public void sendEvent(UUID bulletinId, BulletinEvent event) throws BindException {
        StateMachine<BulletinState, BulletinEvent> machine = restore(bulletinId);
        machine.sendEvent(event);

        String currentState = getBulletin(machine).getState().name();
        log.info("Итоговое состояние bulletin: {}", currentState);

        BindingResult errors = getValidationErrors(machine);
        log.info(errors.hasErrors() ? "Ошибки валидации." : "");
        if (errors.hasErrors()) {
            throw new BindException(errors);
        }
    }

    @Override
    public StateMachine<BulletinState, BulletinEvent> restore(UUID bulletinId) {
        Bulletin bulletin = bulletinRepository.findById(bulletinId)
                .orElseThrow(() -> new ResourceNotFoundException("Bulletin not found"));

        StateMachine<BulletinState, BulletinEvent> machine =
                factory.getStateMachine(bulletinId.toString());

        machine.stop();

        machine.getStateMachineAccessor().doWithAllRegions(access -> {
            access.addStateMachineInterceptor(stateChangeListener);

            StateMachineContext<BulletinState, BulletinEvent> context =
                    new DefaultStateMachineContext<>(
                            bulletin.getState(),
                            null,
                            createExtendedState(bulletin),
                            null,
                            null
                    );
            access.resetStateMachine(context);
        });

        machine.start();

        log.info("После start() - запущена, состояние: {}", machine.getState());

        machine.getExtendedState().getVariables()
                .put(BulletinSMHeaderContract.BULLETIN_HEADER, bulletin);
        machine.getExtendedState().getVariables()
                .put(BulletinSMHeaderContract.BULLETIN_ID_HEADER, bulletin.getId());


        return machine;
    }

    private Map<String, Object> createExtendedState(Bulletin bulletin) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(BulletinSMHeaderContract.BULLETIN_ID_HEADER, bulletin.getId());
        variables.put(BulletinSMHeaderContract.BULLETIN_HEADER, bulletin);

        Errors emptyErrorsHoled = new BeanPropertyBindingResult(Bulletin.class, "bulletin");
        variables.put(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER, emptyErrorsHoled);
        return variables;
    }

    private BindingResult getValidationErrors(StateMachine<BulletinState, BulletinEvent> machine) {
        return (BindingResult) machine.getExtendedState()
                .getVariables()
                .get(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER);
    }

    private Bulletin getBulletin(StateMachine<BulletinState, BulletinEvent> machine) {
        return machine.getExtendedState()
                .get(BulletinSMHeaderContract.BULLETIN_HEADER, Bulletin.class);
    }

}
