package com.example.bulletin.application.statemachine.bulletin.action;

import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import org.springframework.statemachine.action.Action;

public interface BulletinActions {
    Action<BulletinState, BulletinEvent> updateAction();
    Action<BulletinState, BulletinEvent> addImage();
    Action<BulletinState, BulletinEvent> removeImage();
    Action<BulletinState, BulletinEvent> setMainImage();

}
