package com.example.bulletin.application.statemachine.bulletin;

import com.example.bulletin.application.statemachine.bulletin.contract.BulletinExtendedState;
import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import com.example.bulletin.infrastructure.repository.BulletinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BulletinStateChangeListener extends StateMachineInterceptorAdapter<BulletinState, BulletinEvent> {

    private final BulletinRepository repository;

    @Override
    public void postStateChange(State<BulletinState, BulletinEvent> state, Message<BulletinEvent> message, Transition<BulletinState, BulletinEvent> transition,
                                StateMachine<BulletinState, BulletinEvent> stateMachine, StateMachine<BulletinState, BulletinEvent> rootStateMachine) {
        if (message == null) { return; }
        log.info("Вызван postStateChange при состоянии: {}", state.getId().name());
        Bulletin bulletin = getBulletinFromMachine(stateMachine);
        bulletin.setState(state.getId());
        repository.save(bulletin);
    }

    private Bulletin getBulletinFromMachine(StateMachine<BulletinState, BulletinEvent> stateMachine) {
        if (stateMachine == null || stateMachine.getExtendedState() == null) {
            return null;
        }
        return stateMachine.getExtendedState()
                .get(BulletinExtendedState.BULLETIN, Bulletin.class);
    }

}


