package com.example.bulletin.application.service.tradeaccount;

import com.example.bulletin.application.service.tradeaccount.data.request.*;
import com.example.bulletin.application.service.tradeaccount.data.response.*;

public interface TradeAccountService {
    GetTradeAccountResponse getTradeAccount(GetTradeAccountRequest request);
    CreateTradeAccountResponse createTradeAccount(CreateTradeAccountRequest request);
    RenameTradeAccountResponse renameTradeAccount(RenameTradeAccountRequest request);
    ChangePhoneTradeAccountResponse changePhone(ChangePhoneTradeAccountRequest request);
    ChangeContactsTradeAccountResponse changeContacts(ChangeContactsTradeAccountRequest request);
    ChangeDescriptionTradeAccountResponse changeDescription(ChangeDescriptionTradeAccountRequest request);
    SetApproximateLocationTradeAccountResponse setApproximateLocation(SetApproximateLocationTradeAccountRequest request);
    SetExactLocationTradeAccountResponse SetExactLocation(SetExactLocationTradeAccountRequest request);
    ApproveTradeAccountResponse approveTradeAccount(ApproveTradeAccountRequest request);
}
