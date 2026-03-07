package com.example.bulletin.application.statemachine.bulletin.service;

import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import org.springframework.statemachine.StateMachine;
import org.springframework.validation.BindException;

import java.util.UUID;

public interface BulletinStateMachineService {
    void sendEvent(UUID bulletinId, BulletinEvent event) throws BindException;
    StateMachine<BulletinState, BulletinEvent> restore(UUID bulletinId);
}
