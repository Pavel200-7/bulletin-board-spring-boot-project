package com.example.bulletin.application.statemachine.bulletin.service;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.statemachine.bulletin.BulletinStateChangeListener;
import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import com.example.bulletin.infrastructure.repository.BulletinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BulletinStateMachineServiceImpl implements BulletinStateMachineService {

    private final StateMachineFactory<BulletinState, BulletinEvent> factory;
    private final BulletinRepository bulletinRepository;
    private final BulletinStateChangeListener stateChangeListener;

    @Override
    public StateMachine<BulletinState, BulletinEvent> restore(UUID bulletinId) {
        Bulletin bulletin = bulletinRepository.findById(bulletinId)
                .orElseThrow(() -> new ResourceNotFoundException("Bulletin not found"));

        StateMachine<BulletinState, BulletinEvent> machine =
                factory.getStateMachine(bulletinId.toString());

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

        return machine;
    }

    private Map<String, Object> createExtendedState(Bulletin bulletin) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("bulletinId", bulletin.getId());
        variables.put("bulletin", bulletin);
        return variables;
    }

}
