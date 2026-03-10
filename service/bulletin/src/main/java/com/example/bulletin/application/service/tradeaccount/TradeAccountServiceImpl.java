package com.example.bulletin.application.service.tradeaccount;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.TradeAccountMapper;
import com.example.bulletin.application.service.tradeaccount.data.request.*;
import com.example.bulletin.application.service.tradeaccount.data.response.*;
import com.example.bulletin.application.data.response.TradeAccountResponse;
import com.example.bulletin.application.service.bulletin.helper.specification.BulletinSpecificationBuilder;
import com.example.bulletin.domain.entity.TradeAccount;
import com.example.bulletin.domain.entity.base.Location;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.infrastructure.repository.TradeAccountRepository;
import com.example.bulletin.infrastructure.repository.UserRepository;
import com.example.bulletin.infrastructure.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeAccountServiceImpl implements TradeAccountService {

    private final TradeAccountRepository tradeAccountRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final TradeAccountMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public GetTradeAccountResponse getTradeAccount(GetTradeAccountRequest request) {
        TradeAccount tradeAccount = tradeAccountRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Trade account not found with id: " + request.getId()));

        TradeAccountResponse response = mapper.toResponse(tradeAccount);
        return new GetTradeAccountResponse(response);
    }

    @Override
    @Transactional
    public CreateTradeAccountResponse createTradeAccount(CreateTradeAccountRequest request) {
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getOwnerId()));

        OwnerInfo ownerInfo = new OwnerInfo(owner);
        TradeAccount tradeAccount = TradeAccount.createTradeAccount(ownerInfo);
        tradeAccountRepository.save(tradeAccount);

        TradeAccountResponse response = mapper.toResponse(tradeAccount);
        return new CreateTradeAccountResponse(response);
    }

    @Override
    @Transactional
    public RenameTradeAccountResponse renameTradeAccount(RenameTradeAccountRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        TradeAccount tradeAccount = tradeAccountRepository.findByOwnerInfo_Owner_Id(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Trade account not found with id: " + currentUserId));

        tradeAccount.setName(request.getName());
        tradeAccountRepository.save(tradeAccount);

        TradeAccountResponse response = mapper.toResponse(tradeAccount);
        return new RenameTradeAccountResponse(response);
    }

    @Override
    @Transactional
    public ChangePhoneTradeAccountResponse changePhone(ChangePhoneTradeAccountRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        TradeAccount tradeAccount = tradeAccountRepository.findByOwnerInfo_Owner_Id(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Trade account not found with id: " + currentUserId));

        tradeAccount.setPhone(request.getPhone());
        tradeAccountRepository.save(tradeAccount);

        TradeAccountResponse response = mapper.toResponse(tradeAccount);
        return new ChangePhoneTradeAccountResponse(response);
    }

    @Override
    @Transactional
    public ChangeContactsTradeAccountResponse changeContacts(ChangeContactsTradeAccountRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        TradeAccount tradeAccount = tradeAccountRepository.findByOwnerInfo_Owner_Id(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Trade account not found with id: " + currentUserId));

        tradeAccount.setContacts(request.getContacts());
        tradeAccountRepository.save(tradeAccount);

        TradeAccountResponse response = mapper.toResponse(tradeAccount);
        return new ChangeContactsTradeAccountResponse(response);
    }

    @Override
    @Transactional
    public ChangeDescriptionTradeAccountResponse changeDescription(ChangeDescriptionTradeAccountRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        TradeAccount tradeAccount = tradeAccountRepository.findByOwnerInfo_Owner_Id(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Trade account not found with id: " + currentUserId));

        tradeAccount.setDescription(request.getDescription());
        tradeAccountRepository.save(tradeAccount);

        TradeAccountResponse response = mapper.toResponse(tradeAccount);
        return new ChangeDescriptionTradeAccountResponse(response);
    }

    @Override
    @Transactional
    public SetApproximateLocationTradeAccountResponse setApproximateLocation(SetApproximateLocationTradeAccountRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        TradeAccount tradeAccount = tradeAccountRepository.findByOwnerInfo_Owner_Id(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Trade account not found with id: " + currentUserId));

        Location location = new Location(
                request.getLatitude(),
                request.getLongitude(),
                request.getTownName(),
                ""
        );

        tradeAccount.setApproximateLocation(location);
        tradeAccountRepository.save(tradeAccount);

        TradeAccountResponse response = mapper.toResponse(tradeAccount);
        return new SetApproximateLocationTradeAccountResponse(response);
    }

    @Override
    @Transactional
    public SetExactLocationTradeAccountResponse SetExactLocation(SetExactLocationTradeAccountRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        TradeAccount tradeAccount = tradeAccountRepository.findByOwnerInfo_Owner_Id(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Trade account not found with id: " + currentUserId));

        Location location = new Location(
                request.getLatitude(),
                request.getLongitude(),
                request.getTownName(),
                request.getLocationName()
        );

        tradeAccount.setExactLocation(location);
        tradeAccountRepository.save(tradeAccount);

        TradeAccountResponse response = mapper.toResponse(tradeAccount);
        return new SetExactLocationTradeAccountResponse(response);
    }

    @Override
    public ApproveTradeAccountResponse approveTradeAccount(ApproveTradeAccountRequest request) {
        UUID currentUserId = securityService.getCurrentUserIdAsUUID();
        TradeAccount tradeAccount = tradeAccountRepository.findByOwnerInfo_Owner_Id(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Trade account not found with id: " + currentUserId));

        tradeAccount.approve();
        tradeAccountRepository.save(tradeAccount);

        TradeAccountResponse response = mapper.toResponse(tradeAccount);
        return new ApproveTradeAccountResponse(response);
    }

}