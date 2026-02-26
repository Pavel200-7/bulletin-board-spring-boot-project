package com.example.bulletin.host.controller;

import com.example.bulletin.application.service.characteristicvalue.CharacteristicValueService;
import com.example.bulletin.application.service.characteristicvalue.data.request.CreateCharacteristicValueRequest;
import com.example.bulletin.application.service.characteristicvalue.data.request.DeleteCharacteristicValueRequest;
import com.example.bulletin.application.service.characteristicvalue.data.request.GetCharacteristicValuesRequest;
import com.example.bulletin.application.service.characteristicvalue.data.response.CreateCharacteristicValueResponse;
import com.example.bulletin.application.service.characteristicvalue.data.response.GetCharacteristicValuesResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/characteristic/{characteristicId}/characteristic-value")
public class CharacteristicCharacteristicValueController {

    private final CharacteristicValueService service;

    @GetMapping
    public ResponseEntity<GetCharacteristicValuesResponse> getCharacteristicValues(@PathVariable UUID characteristicId) {
        GetCharacteristicValuesRequest request = new GetCharacteristicValuesRequest(characteristicId);
        return ResponseEntity.ok(service.getCharacteristicValues(request));
    }

    @PostMapping
    public ResponseEntity<CreateCharacteristicValueResponse> createCharacteristicValue(
            @Valid @RequestBody CreateCharacteristicValueRequest request,
            @PathVariable UUID characteristicId) {
        request.setCharacteristicId(characteristicId);
        return ResponseEntity.ok(service.createCharacteristicValue(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharacteristicValue(
            @Valid @PathVariable UUID characteristicId,
            @Valid @PathVariable UUID id) {
        service.deleteCharacteristicValue(new DeleteCharacteristicValueRequest(characteristicId, id));
        return ResponseEntity.noContent().build();
    }

}
