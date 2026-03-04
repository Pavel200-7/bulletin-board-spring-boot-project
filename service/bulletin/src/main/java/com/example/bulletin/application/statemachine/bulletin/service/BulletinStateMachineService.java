package com.example.bulletin.application.statemachine.bulletin.service;

import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import org.springframework.statemachine.StateMachine;

import java.util.UUID;

public interface BulletinStateMachineService {
    StateMachine<BulletinState, BulletinEvent> restore(UUID bulletinId);
}
