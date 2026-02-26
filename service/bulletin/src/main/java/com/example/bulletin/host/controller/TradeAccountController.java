package com.example.bulletin.host.controller;

import com.example.bulletin.application.service.tradeaccount.TradeAccountService;
import com.example.bulletin.application.service.tradeaccount.data.request.*;
import com.example.bulletin.application.service.tradeaccount.data.response.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trade-account")
public class TradeAccountController {

    private final TradeAccountService service;

    @GetMapping("/{id}")
    public ResponseEntity<GetTradeAccountResponse> getTradeAccount(@PathVariable UUID id) {
        GetTradeAccountRequest request = new GetTradeAccountRequest(id);
        return ResponseEntity.ok(service.getTradeAccount(request));
    }

    @PutMapping("/name")
    public ResponseEntity<RenameTradeAccountResponse> changeName(@Valid @RequestBody RenameTradeAccountRequest request) {
        return ResponseEntity.ok(service.renameTradeAccount(request));
    }

    @PutMapping("/phone")
    public ResponseEntity<ChangePhoneTradeAccountResponse> changePhone(@Valid @RequestBody ChangePhoneTradeAccountRequest request) {
        return ResponseEntity.ok(service.changePhone(request));
    }

    @PutMapping("/contacts")
    public ResponseEntity<ChangeContactsTradeAccountResponse> changeContacts(@Valid @RequestBody ChangeContactsTradeAccountRequest request) {
        return ResponseEntity.ok(service.changeContacts(request));
    }

    @PutMapping("/description")
    public ResponseEntity<ChangeDescriptionTradeAccountResponse> changeDescription(@Valid @RequestBody ChangeDescriptionTradeAccountRequest request) {
        return ResponseEntity.ok(service.changeDescription(request));
    }

    @PutMapping("/approximate-location")
    public ResponseEntity<SetApproximateLocationTradeAccountResponse> setApproximateLocation(
            @Valid @RequestBody SetApproximateLocationTradeAccountRequest request) {
        return ResponseEntity.ok(service.setApproximateLocation(request));
    }

    @PutMapping("/exact-location")
    public ResponseEntity<SetExactLocationTradeAccountResponse> setExactLocation(
            @Valid @RequestBody SetExactLocationTradeAccountRequest request) {
        return ResponseEntity.ok(service.SetExactLocation(request));
    }

}
