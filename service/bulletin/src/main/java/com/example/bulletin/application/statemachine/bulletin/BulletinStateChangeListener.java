package com.example.bulletin.application.statemachine.bulletin;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.statemachine.bulletin.contract.BulletinSMHeaderContract;
import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import com.example.bulletin.infrastructure.repository.BulletinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BulletinStateChangeListener extends StateMachineInterceptorAdapter<BulletinState, BulletinEvent> {

    private final BulletinRepository repository;

    @Override
    public void postStateChange(State<BulletinState, BulletinEvent> state, Message<BulletinEvent> message, Transition<BulletinState, BulletinEvent> transition,
                                StateMachine<BulletinState, BulletinEvent> stateMachine, StateMachine<BulletinState, BulletinEvent> rootStateMachine) {
        if (message == null) { return; }
        UUID bulletinId = UUID.class.cast(
                message.getHeaders().get(BulletinSMHeaderContract.BULLETIN_ID_HEADER));
        Bulletin bulletin = repository.findById(bulletinId)
                .orElseThrow(() -> new ResourceNotFoundException("Bulletin not found"));

        bulletin.setState(state.getId());
        repository.save(bulletin);
    }

}


