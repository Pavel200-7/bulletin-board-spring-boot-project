package com.example.bulletin.application.statemachine.bulletin.guard;

import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.domain.enums.bulletin.BulletinState;

import org.springframework.statemachine.guard.Guard;

public interface BulletinGuards {
    Guard<BulletinState, BulletinEvent> checkIfCanBeApprovedGuard();
    Guard<BulletinState, BulletinEvent> checkIfUserCanBeABulletinPublisherGuard();
    Guard<BulletinState, BulletinEvent> checkIfUserIsOwnerGuard();
    Guard<BulletinState, BulletinEvent> checkIfUserIsAdminGuard();
}
