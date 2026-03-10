package com.example.bulletin.application.service.bulletin;

import com.example.bulletin.application.data.request.BulletinRequest;
import com.example.bulletin.application.data.response.BulletinResponse;
import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.BulletinMapper;
import com.example.bulletin.application.service.bulletin.data.request.*;
import com.example.bulletin.application.service.bulletin.data.response.*;
import com.example.bulletin.application.statemachine.bulletin.contract.BulletinMessageState;
import com.example.bulletin.application.statemachine.bulletin.service.BulletinStateMachineService;
import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.infrastructure.repository.BulletinRepository;
import com.example.bulletin.infrastructure.repository.UserRepository;
import com.example.bulletin.infrastructure.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BulletinServiceImpl implements BulletinService {

    private final BulletinRepository bulletinRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final BulletinStateMachineService stateMachineService;
    private final BulletinMapper mapper;

    @Override
    public CreateBulletinResponse createBulletin(CreateBulletinRequest request)
            throws Exception {
        OwnerInfo ownerInfo = getOwnerInfo();
        Bulletin bulletin = Bulletin.createDraft(ownerInfo);
        bulletinRepository.save(bulletin);

        Message<BulletinEvent> message = MessageBuilder
                .withPayload(BulletinEvent.APPROVE)
                .setHeader(BulletinMessageState.BULLETIN_ID, bulletin.getId())
                .build();

        stateMachineService.sendEvent(message);

        Optional<Bulletin> modifiableBulletin = bulletinRepository.findById(bulletin.getId());
        BulletinResponse bulletinResponse = mapper.toResponse(modifiableBulletin.get());
        return new CreateBulletinResponse(bulletinResponse);
    }

    @Override
    public UpdateBulletinResponse updateBulletin(UpdateBulletinRequest request) throws Exception {
        BulletinRequest bulletinRequest = request.getBulletinRequest();
        Message<BulletinEvent> message = MessageBuilder
                .withPayload(BulletinEvent.UPDATE)
                .setHeader(BulletinMessageState.BULLETIN_ID, bulletinRequest.getId())
                .setHeader(BulletinMessageState.BULLETIN_UPDATE_REQUEST, bulletinRequest)
                .build();
        stateMachineService.sendEvent(message);

        Optional<Bulletin> modifiableBulletin = bulletinRepository.findById(request.getBulletinRequest().getId());
        BulletinResponse bulletinResponse = mapper.toResponse(modifiableBulletin.get());
        return new UpdateBulletinResponse(bulletinResponse);
    }

    @Override
    public ApproveBulletinResponse approveBulletin(ApproveBulletinRequest request) throws Exception {
        Message<BulletinEvent> message = MessageBuilder
                .withPayload(BulletinEvent.APPROVE)
                .setHeader(BulletinMessageState.BULLETIN_ID, request.getBulletinId())
                .build();
        stateMachineService.sendEvent(message);

        Optional<Bulletin> modifiableBulletin = bulletinRepository.findById(request.getBulletinId());
        BulletinResponse bulletinResponse = mapper.toResponse(modifiableBulletin.get());
        return new ApproveBulletinResponse(bulletinResponse);
    }

    @Override
    public PublishBulletinResponse publishBulletin(PublishBulletinRequest request) throws Exception {
        Message<BulletinEvent> message = MessageBuilder
                .withPayload(BulletinEvent.APPROVE)
                .setHeader(BulletinMessageState.BULLETIN_ID, request.getBulletinId())
                .build();
        stateMachineService.sendEvent(message);

        Optional<Bulletin> modifiableBulletin = bulletinRepository.findById(request.getBulletinId());
        BulletinResponse bulletinResponse = mapper.toResponse(modifiableBulletin.get());
        return new PublishBulletinResponse(bulletinResponse);
    }

    @Override
    public CloseBulletinResponse closeBulletin(CloseBulletinRequest request) throws Exception {
        Message<BulletinEvent> message = MessageBuilder
                .withPayload(BulletinEvent.APPROVE)
                .setHeader(BulletinMessageState.BULLETIN_ID, request.getBulletinId())
                .build();
        stateMachineService.sendEvent(message);

        Optional<Bulletin> modifiableBulletin = bulletinRepository.findById(request.getBulletinId());
        BulletinResponse bulletinResponse = mapper.toResponse(modifiableBulletin.get());
        return new CloseBulletinResponse(bulletinResponse);
    }

    private OwnerInfo getOwnerInfo() {
        UUID userId = securityService.getCurrentUserIdAsUUID();
        Optional<User> user = userRepository.findById(userId);
        return new OwnerInfo(user.get());
    }

}
