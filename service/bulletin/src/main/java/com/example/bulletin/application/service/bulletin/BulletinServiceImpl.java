package com.example.bulletin.application.service.bulletin;

import com.example.bulletin.application.data.request.BulletinRequest;
import com.example.bulletin.application.data.response.BulletinResponse;
import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.BulletinMapper;
import com.example.bulletin.application.service.bulletin.data.request.*;
import com.example.bulletin.application.service.bulletin.data.request.data.BulletinSearchCriteria;
import com.example.bulletin.application.service.bulletin.data.request.data.PageData;
import com.example.bulletin.application.service.bulletin.data.response.*;
import com.example.bulletin.application.service.bulletin.data.response.data.BulletinPaginationData;
import com.example.bulletin.application.service.bulletin.helper.specification.BulletinSpecificationBuilder;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BulletinServiceImpl implements BulletinService {

    private final BulletinRepository bulletinRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final BulletinStateMachineService stateMachineService;
    private final BulletinSpecificationBuilder specificationBuilder;
    private final BulletinMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public GetBulletinResponse getBulletin(GetBulletinRequest request) {
        Bulletin bulletin = bulletinRepository.findByIdEager(request.getBulletinId())
                .orElseThrow(() -> new ResourceNotFoundException("Bulletin not found."));
        if (!bulletin.isActive()) {
            throw new ResourceNotFoundException("Bulletin not found.");
        }

        BulletinResponse bulletinResponse = mapper.toResponse(bulletin);
        return new GetBulletinResponse(bulletinResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public GetBulletinPaginationResponse getBulletinPagination(GetBulletinPaginationRequest request) {
        log.info(request.toString());

        BulletinSearchCriteria criteria = request.getCriteria();
        Specification<Bulletin> spec = specificationBuilder.fromCriteria(criteria);

        PageData pageData = request.getPageData();
        Sort sortOrder = Sort.by(criteria.getDirection(), criteria.getOrderBy().getFieldName());

        Pageable pageable = PageRequest.of(pageData.getPage(), pageData.getSize(), sortOrder);
        Page<Bulletin> bulletins = bulletinRepository.findAll(spec, pageable);

        Page<BulletinPaginationData> paginationData = bulletins.map(mapper::toPaginationData);
        return new GetBulletinPaginationResponse(paginationData);
    }

    @Override
    @Transactional(readOnly = true)
    public GetModifiableBulletinResponse getModifiableBulletin(GetModifiableBulletinRequest request) {
        Bulletin bulletin = bulletinRepository.findByIdEager(request.getBulletinId())
                .orElseThrow(() -> new ResourceNotFoundException("Bulletin not found."));

        OwnerInfo ownerInfo = getOwnerInfo();
        if (!bulletin.getOwnerInfo()
                .isOwnedByUser(ownerInfo.getOwner())) {
            throw new ResourceNotFoundException("Bulletin not found.");
        }

        BulletinResponse bulletinResponse = mapper.toResponse(bulletin);
        return new GetModifiableBulletinResponse(bulletinResponse);
    }

    @Override
    @Transactional
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
    @Transactional
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
    @Transactional
    public AddBulletinImageResponse addImage(AddBulletinImageRequest request) throws Exception {
        Message<BulletinEvent> message = MessageBuilder
                .withPayload(BulletinEvent.ADD_IMAGE)
                .setHeader(BulletinMessageState.BULLETIN_ID, request.getBulletinId())
                .setHeader(BulletinMessageState.BULLETIN_PROVIDER_IMAGE_ID, request.getProviderImageId())
                .build();
        stateMachineService.sendEvent(message);

        Optional<Bulletin> modifiableBulletin = bulletinRepository.findById(request.getBulletinId());
        BulletinResponse bulletinResponse = mapper.toResponse(modifiableBulletin.get());
        return new AddBulletinImageResponse(bulletinResponse);
    }

    @Override
    @Transactional
    public RemoveBulletinImageResponse removeImage(RemoveBulletinImageRequest request) throws Exception {
        Message<BulletinEvent> message = MessageBuilder
                .withPayload(BulletinEvent.REMOVE_IMAGE)
                .setHeader(BulletinMessageState.BULLETIN_ID, request.getBulletinId())
                .setHeader(BulletinMessageState.BULLETIN_IMAGE_ID, request.getImageId())
                .build();
        stateMachineService.sendEvent(message);

        Optional<Bulletin> modifiableBulletin = bulletinRepository.findById(request.getBulletinId());
        BulletinResponse bulletinResponse = mapper.toResponse(modifiableBulletin.get());
        return new RemoveBulletinImageResponse(bulletinResponse);
    }

    @Override
    @Transactional
    public SetMainBulletinImageResponse setMainImage(SetMainBulletinImageRequest request) throws Exception {
        Message<BulletinEvent> message = MessageBuilder
                .withPayload(BulletinEvent.SET_MAIN_IMAGE)
                .setHeader(BulletinMessageState.BULLETIN_ID, request.getBulletinId())
                .setHeader(BulletinMessageState.BULLETIN_IMAGE_ID, request.getImageId())
                .build();
        stateMachineService.sendEvent(message);

        Optional<Bulletin> modifiableBulletin = bulletinRepository.findById(request.getBulletinId());
        BulletinResponse bulletinResponse = mapper.toResponse(modifiableBulletin.get());
        return new SetMainBulletinImageResponse(bulletinResponse);
    }

    @Override
    @Transactional
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
    @Transactional
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
    @Transactional
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
