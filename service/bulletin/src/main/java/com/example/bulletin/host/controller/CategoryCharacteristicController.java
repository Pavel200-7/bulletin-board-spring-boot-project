package com.example.bulletin.host.controller;

import com.example.bulletin.application.service.characteristic.CharacteristicService;
import com.example.bulletin.application.service.characteristic.data.request.CreateCharacteristicRequest;
import com.example.bulletin.application.service.characteristic.data.request.DeleteCharacteristicRequest;
import com.example.bulletin.application.service.characteristic.data.request.GetCategoryCharacteristicsRequest;
import com.example.bulletin.application.service.characteristic.data.response.CreateCharacteristicResponse;
import com.example.bulletin.application.service.characteristic.data.response.GetCategoryCharacteristicsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category/{categoryId}/characteristic")
public class CategoryCharacteristicController {

    private final CharacteristicService service;

    @GetMapping
    public ResponseEntity<GetCategoryCharacteristicsResponse> getCategoryCharacteristics(@PathVariable UUID categoryId) {
        GetCategoryCharacteristicsRequest request = new GetCategoryCharacteristicsRequest(categoryId);
        return ResponseEntity.ok(service.getCategoryCharacteristics(request));
    }

    @PostMapping
    public ResponseEntity<CreateCharacteristicResponse> createCharacteristic(
            @Valid @RequestBody CreateCharacteristicRequest request,
            @Valid @PathVariable UUID categoryId) {
        request.setCategoryId(categoryId);
        return ResponseEntity.ok(service.createCharacteristic(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoryCharacteristic(
            @Valid @PathVariable UUID categoryId,
            @Valid @PathVariable UUID id) {
        service.deleteCharacteristic(new DeleteCharacteristicRequest(categoryId, id));
        return ResponseEntity.noContent().build();
    }

}
