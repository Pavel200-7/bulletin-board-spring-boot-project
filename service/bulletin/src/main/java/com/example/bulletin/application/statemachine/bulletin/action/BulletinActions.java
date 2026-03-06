package com.example.bulletin.application.statemachine.bulletin.action;

import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import org.springframework.statemachine.action.Action;

public interface BulletinActions {
    Action<BulletinState, BulletinEvent> setModifiableAction();
    Action<BulletinState, BulletinEvent> setApprovedAction();
    Action<BulletinState, BulletinEvent> setPublishedAction();
    Action<BulletinState, BulletinEvent> setCompletedAction();
}
