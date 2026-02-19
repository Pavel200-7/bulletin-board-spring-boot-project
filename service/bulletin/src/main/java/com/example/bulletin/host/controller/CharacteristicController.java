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

    @GetMapping("/category_characteristics/{categoryId}")
    public ResponseEntity<GetCategoryCharacteristicsResponse> getCategoryCharacteristics(@PathVariable UUID categoryId) {
        GetCategoryCharacteristicsRequest request = new GetCategoryCharacteristicsRequest(categoryId);
        return ResponseEntity.ok(service.getCategoryCharacteristics(request));
    }

    @PostMapping
    public ResponseEntity<CreateCharacteristicResponse> createCharacteristic(@Valid @RequestBody CreateCharacteristicRequest request) {
        return ResponseEntity.ok(service.createCharacteristic(request));
    }

    @PutMapping("/name")
    public ResponseEntity<RenameCharacteristicResponse> renameCharacteristic(@Valid @RequestBody RenameCharacteristicRequest request) {
        return ResponseEntity.ok(service.renameCharacteristic(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharacteristic(@Valid @PathVariable UUID id) {
        service.deleteCharacteristic(new DeleteCharacteristicRequest(id));
        return ResponseEntity.noContent().build();
    }

}
