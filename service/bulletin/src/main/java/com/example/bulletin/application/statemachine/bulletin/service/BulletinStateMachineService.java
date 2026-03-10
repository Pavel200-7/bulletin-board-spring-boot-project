package com.example.bulletin.application.statemachine.bulletin.service;

import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.validation.BindException;

import java.util.UUID;

public interface BulletinStateMachineService {
    void sendEvent(Message<BulletinEvent> message) throws Exception;
    StateMachine<BulletinState, BulletinEvent> restore(UUID bulletinId);
}
