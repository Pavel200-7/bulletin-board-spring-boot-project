package com.example.bulletin.host.controller;

import com.example.bulletin.application.service.characteristicvalue.CharacteristicValueService;
import com.example.bulletin.application.service.characteristicvalue.data.request.*;
import com.example.bulletin.application.service.characteristicvalue.data.response.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/characteristic_value")
public class CharacteristicValueController {

    private final CharacteristicValueService service;

    @GetMapping("/{id}")
    public ResponseEntity<GetCharacteristicValueResponse> getCharacteristicValue(@PathVariable UUID id) {
        GetCharacteristicValueRequest request = new GetCharacteristicValueRequest(id);
        return ResponseEntity.ok(service.getCharacteristicValue(request));
    }

    @GetMapping("/by_characteristic/{characteristicId}")
    public ResponseEntity<GetCharacteristicValuesResponse> getCharacteristicValues(@PathVariable UUID characteristicId) {
        GetCharacteristicValuesRequest request = new GetCharacteristicValuesRequest(characteristicId);
        return ResponseEntity.ok(service.getCharacteristicValues(request));
    }

    @PostMapping
    public ResponseEntity<CreateCharacteristicValueResponse> createCharacteristicValue(@Valid @RequestBody CreateCharacteristicValueRequest request) {
        return ResponseEntity.ok(service.createCharacteristicValue(request));
    }

    @PutMapping("/name")
    public ResponseEntity<RenameCharacteristicValueResponse> renameCharacteristicValue(@Valid @RequestBody RenameCharacteristicValueRequest request) {
        return ResponseEntity.ok(service.renameCharacteristicValue(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharacteristicValue(@Valid @PathVariable UUID id) {
        service.deleteCharacteristicValue(new DeleteCharacteristicValueRequest(id));
        return ResponseEntity.noContent().build();
    }
}
