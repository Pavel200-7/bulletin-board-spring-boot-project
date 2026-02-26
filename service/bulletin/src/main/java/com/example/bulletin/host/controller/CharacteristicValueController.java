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
@RequestMapping("/api/v1/characteristic-value")
public class CharacteristicValueController {

    private final CharacteristicValueService service;

    @GetMapping("/{id}")
    public ResponseEntity<GetCharacteristicValueResponse> getCharacteristicValue(@PathVariable UUID id) {
        GetCharacteristicValueRequest request = new GetCharacteristicValueRequest(id);
        return ResponseEntity.ok(service.getCharacteristicValue(request));
    }

    @PutMapping("/name")
    public ResponseEntity<RenameCharacteristicValueResponse> renameCharacteristicValue(@Valid @RequestBody RenameCharacteristicValueRequest request) {
        return ResponseEntity.ok(service.renameCharacteristicValue(request));
    }

}
