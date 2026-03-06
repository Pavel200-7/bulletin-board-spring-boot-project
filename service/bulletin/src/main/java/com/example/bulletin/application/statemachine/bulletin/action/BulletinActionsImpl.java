package com.example.bulletin.application.statemachine.bulletin.action;

import com.example.bulletin.application.statemachine.bulletin.contract.BulletinSMHeaderContract;
import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.statemachine.action.Action;

@Component
@RequiredArgsConstructor
public class BulletinActionsImpl implements BulletinActions {

    @Override
    public Action<BulletinState, BulletinEvent> setModifiableAction() {
        return context -> {
            Bulletin bulletin = context.getExtendedState()
                    .get(BulletinSMHeaderContract.BULLETIN_HEADER, Bulletin.class);
            if (bulletin != null) {
                bulletin.setState(BulletinState.MODIFIABLE);
            }
        };
    }

    @Override
    public Action<BulletinState, BulletinEvent> setApprovedAction() {
        return context -> {
            Bulletin bulletin = context.getExtendedState()
                    .get(BulletinSMHeaderContract.BULLETIN_HEADER, Bulletin.class);
            if (bulletin != null) {
                bulletin.setState(BulletinState.APPROVED);
            }
        };
    }

    @Override
    public Action<BulletinState, BulletinEvent> setPublishedAction() {
        return context -> {
            Bulletin bulletin = context.getExtendedState()
                    .get(BulletinSMHeaderContract.BULLETIN_HEADER, Bulletin.class);
            if (bulletin != null) {
                bulletin.setState(BulletinState.PUBLISHED);
            }
        };
    }

    @Override
    public Action<BulletinState, BulletinEvent> setCompletedAction() {
        return context -> {
            Bulletin bulletin = context.getExtendedState()
                    .get(BulletinSMHeaderContract.BULLETIN_HEADER, Bulletin.class);
            if (bulletin != null) {
                bulletin.setState(BulletinState.COMPLETED);
            }
        };
    }

}
