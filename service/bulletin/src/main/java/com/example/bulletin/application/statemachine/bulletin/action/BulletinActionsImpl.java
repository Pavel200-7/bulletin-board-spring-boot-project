package com.example.bulletin.application.statemachine.bulletin.action;

import com.example.bulletin.application.data.request.BulletinRequest;
import com.example.bulletin.application.statemachine.bulletin.action.helper.BulletinModifyService;
import com.example.bulletin.application.statemachine.bulletin.contract.BulletinExtendedState;
import com.example.bulletin.application.statemachine.bulletin.contract.BulletinMessageState;
import com.example.bulletin.application.statemachine.bulletin.guard.helper.BulletinValidationContext;
import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BulletinActionsImpl implements BulletinActions {

    private final BulletinModifyService modifyService;

    @Override
    public Action<BulletinState, BulletinEvent> updateAction() {
        return context -> {
            log.info("Начало updateAction");
            BulletinValidationContext validationContext = new BulletinValidationContext(context);

            Bulletin bulletin = context.getExtendedState()
                    .get(BulletinExtendedState.BULLETIN, Bulletin.class);
            BulletinRequest bulletinRequest = context.getMessage()
                    .getHeaders()
                    .get(BulletinMessageState.BULLETIN_UPDATE_REQUEST, BulletinRequest.class);
            modifyService.updateBulletin(bulletin, bulletinRequest);
            log.info("Завершено updateAction");
        };
    }

}
