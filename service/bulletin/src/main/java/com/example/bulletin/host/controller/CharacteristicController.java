package com.example.bulletin.host.controller;

import com.example.bulletin.application.service.characteristic.CharacteristicService;
import com.example.bulletin.application.service.characteristic.data.request.*;
import com.example.bulletin.application.service.characteristic.data.response.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/characteristic")
public class CharacteristicController {

    private final CharacteristicService service;

    @GetMapping("/{id}")
    public ResponseEntity<GetCharacteristicResponse> getCharacteristic(@PathVariable UUID id) {
        GetCharacteristicRequest request = new GetCharacteristicRequest(id);
        return ResponseEntity.ok(service.getCharacteristic(request));
    }

    @PutMapping("/name")
    public ResponseEntity<RenameCharacteristicResponse> renameCharacteristic(@Valid @RequestBody RenameCharacteristicRequest request) {
        return ResponseEntity.ok(service.renameCharacteristic(request));
    }

}
