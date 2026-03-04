package com.example.bulletin.application.service.tradeaccount;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.TradeAccountMapper;
import com.example.bulletin.application.service.tradeaccount.data.request.*;
import com.example.bulletin.application.service.tradeaccount.data.response.*;
import com.example.bulletin.application.data.response.TradeAccountResponse;
import com.example.bulletin.domain.entity.TradeAccount;
import com.example.bulletin.domain.entity.base.Location;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.infrastructure.repository.TradeAccountRepository;
import com.example.bulletin.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeAccountServiceImpl implements TradeAccountService {

    private final TradeAccountRepository tradeAccountRepository;
    private final UserRepository userRepository;
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
        tradeAccount = tradeAccountRepository.save(tradeAccount);

        TradeAccountResponse response = mapper.toResponse(tradeAccount);
        return new CreateTradeAccountResponse(response);
    }

    @Override
    @Transactional
    public RenameTradeAccountResponse renameTradeAccount(RenameTradeAccountRequest request) {
        TradeAccount tradeAccount = tradeAccountRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Trade account not found with id: " + request.getId()));

        tradeAccount.setName(request.getName());
        tradeAccount = tradeAccountRepository.save(tradeAccount);

        TradeAccountResponse response = mapper.toResponse(tradeAccount);
        return new RenameTradeAccountResponse(response);
    }

    @Override
    @Transactional
    public ChangePhoneTradeAccountResponse changePhone(ChangePhoneTradeAccountRequest request) {
        TradeAccount tradeAccount = tradeAccountRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Trade account not found with id: " + request.getId()));

        tradeAccount.setPhone(request.getPhone());
        tradeAccount = tradeAccountRepository.save(tradeAccount);

        TradeAccountResponse response = mapper.toResponse(tradeAccount);
        return new ChangePhoneTradeAccountResponse(response);
    }

    @Override
    @Transactional
    public ChangeContactsTradeAccountResponse changeContacts(ChangeContactsTradeAccountRequest request) {
        TradeAccount tradeAccount = tradeAccountRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Trade account not found with id: " + request.getId()));

        tradeAccount.setContacts(request.getContacts());
        tradeAccount = tradeAccountRepository.save(tradeAccount);

        TradeAccountResponse response = mapper.toResponse(tradeAccount);
        return new ChangeContactsTradeAccountResponse(response);
    }

    @Override
    @Transactional
    public ChangeDescriptionTradeAccountResponse changeDescription(ChangeDescriptionTradeAccountRequest request) {
        TradeAccount tradeAccount = tradeAccountRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Trade account not found with id: " + request.getId()));

        tradeAccount.setDescription(request.getDescription());
        tradeAccount = tradeAccountRepository.save(tradeAccount);

        TradeAccountResponse response = mapper.toResponse(tradeAccount);
        return new ChangeDescriptionTradeAccountResponse(response);
    }

    @Override
    @Transactional
    public SetApproximateLocationTradeAccountResponse setApproximateLocation(SetApproximateLocationTradeAccountRequest request) {
        TradeAccount tradeAccount = tradeAccountRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Trade account not found with id: " + request.getId()));

        Location location = new Location(
                request.getLatitude(),
                request.getLongitude(),
                request.getTownName(),
                ""
        );

        tradeAccount.setApproximateLocation(location);
        tradeAccount = tradeAccountRepository.save(tradeAccount);

        TradeAccountResponse response = mapper.toResponse(tradeAccount);
        return new SetApproximateLocationTradeAccountResponse(response);
    }

    @Override
    @Transactional
    public SetExactLocationTradeAccountResponse SetExactLocation(SetExactLocationTradeAccountRequest request) {
        TradeAccount tradeAccount = tradeAccountRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Trade account not found with id: " + request.getId()));

        Location location = new Location(
                request.getLatitude(),
                request.getLongitude(),
                request.getTownName(),
                request.getLocationName()
        );

        tradeAccount.setExactLocation(location);
        tradeAccount = tradeAccountRepository.save(tradeAccount);

        TradeAccountResponse response = mapper.toResponse(tradeAccount);
        return new SetExactLocationTradeAccountResponse(response);
    }

}