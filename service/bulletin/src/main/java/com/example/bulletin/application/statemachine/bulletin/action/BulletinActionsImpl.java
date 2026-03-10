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

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BulletinActionsImpl implements BulletinActions {

    private final BulletinModifyService modifyService;

    @Override
    public Action<BulletinState, BulletinEvent> updateAction() {
        return context -> {
            log.info("Начало updateAction");

            Bulletin bulletin = context.getExtendedState()
                    .get(BulletinExtendedState.BULLETIN, Bulletin.class);
            BulletinRequest bulletinRequest = context.getMessage()
                    .getHeaders()
                    .get(BulletinMessageState.BULLETIN_UPDATE_REQUEST, BulletinRequest.class);
            modifyService.updateBulletin(bulletin, bulletinRequest);

            log.info("Завершено updateAction");
        };
    }

    @Override
    public Action<BulletinState, BulletinEvent> addImage() {
        return context -> {
            log.info("Начало addImage");

            Bulletin bulletin = context.getExtendedState()
                    .get(BulletinExtendedState.BULLETIN, Bulletin.class);
            UUID providerImageId = context.getMessage()
                    .getHeaders()
                    .get(BulletinMessageState.BULLETIN_PROVIDER_IMAGE_ID, UUID.class);
            bulletin.addImage(providerImageId);

            log.info("Завершено addImage");
        };
    }

    @Override
    public Action<BulletinState, BulletinEvent> removeImage() {
        return context -> {
            log.info("Начало removeImage");

            Bulletin bulletin = context.getExtendedState()
                    .get(BulletinExtendedState.BULLETIN, Bulletin.class);
            UUID imageId = context.getMessage()
                    .getHeaders()
                    .get(BulletinMessageState.BULLETIN_IMAGE_ID, UUID.class);
            bulletin.removeImage(imageId);

            log.info("Завершено removeImage");
        };
    }

    @Override
    public Action<BulletinState, BulletinEvent> setMainImage() {
        return context -> {
            log.info("Начало setMainImage");

            Bulletin bulletin = context.getExtendedState()
                    .get(BulletinExtendedState.BULLETIN, Bulletin.class);
            UUID imageId = context.getMessage()
                    .getHeaders()
                    .get(BulletinMessageState.BULLETIN_IMAGE_ID, UUID.class);
            bulletin.setMainImage(imageId);

            log.info("Завершено setMainImage");

        };
    }

}
