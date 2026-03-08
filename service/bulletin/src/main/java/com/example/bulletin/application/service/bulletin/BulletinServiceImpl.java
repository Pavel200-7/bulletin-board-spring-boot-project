package com.example.bulletin.application.service.bulletin;

import com.example.bulletin.application.data.response.BulletinResponse;
import com.example.bulletin.application.mapper.BulletinMapper;
import com.example.bulletin.application.service.bulletin.data.request.ApproveBulletinRequest;
import com.example.bulletin.application.service.bulletin.data.request.CloseBulletinRequest;
import com.example.bulletin.application.service.bulletin.data.request.CreateBulletinRequest;
import com.example.bulletin.application.service.bulletin.data.request.PublishBulletinRequest;
import com.example.bulletin.application.service.bulletin.data.response.ApproveBulletinResponse;
import com.example.bulletin.application.service.bulletin.data.response.CloseBulletinResponse;
import com.example.bulletin.application.service.bulletin.data.response.CreateBulletinResponse;
import com.example.bulletin.application.service.bulletin.data.response.PublishBulletinResponse;
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
            throws BindException {
        OwnerInfo ownerInfo = getOwnerInfo();
        Bulletin bulletin = Bulletin.createDraft(ownerInfo);
        bulletinRepository.save(bulletin);

        stateMachineService.sendEvent(bulletin.getId(),
                BulletinEvent.APPROVE);

        Optional<Bulletin> modifiableBulletin = bulletinRepository.findById(bulletin.getId());
        BulletinResponse bulletinResponse = mapper.toResponse(modifiableBulletin.get());
        return new CreateBulletinResponse(bulletinResponse);
    }

    @Override
    public ApproveBulletinResponse approveBulletin(ApproveBulletinRequest request) throws BindException {
        stateMachineService.sendEvent(request.getBulletinId(),
                BulletinEvent.APPROVE_2);
        Optional<Bulletin> modifiableBulletin = bulletinRepository.findById(request.getBulletinId());
        BulletinResponse bulletinResponse = mapper.toResponse(modifiableBulletin.get());
        return new ApproveBulletinResponse(bulletinResponse);
    }

    @Override
    public PublishBulletinResponse publishBulletin(PublishBulletinRequest request) throws BindException {
        stateMachineService.sendEvent(request.getBulletinId(),
                BulletinEvent.APPROVE);
        Optional<Bulletin> modifiableBulletin = bulletinRepository.findById(request.getBulletinId());
        BulletinResponse bulletinResponse = mapper.toResponse(modifiableBulletin.get());
        return new PublishBulletinResponse(bulletinResponse);
    }

    @Override
    public CloseBulletinResponse closeBulletin(CloseBulletinRequest request) throws BindException {
        stateMachineService.sendEvent(request.getBulletinId(),
                BulletinEvent.APPROVE);
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
