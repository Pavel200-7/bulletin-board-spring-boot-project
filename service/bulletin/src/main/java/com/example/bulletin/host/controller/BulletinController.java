package com.example.bulletin.host.controller;

import com.example.bulletin.application.service.bulletin.BulletinService;
import com.example.bulletin.application.service.bulletin.data.request.CreateBulletinRequest;
import com.example.bulletin.application.service.bulletin.data.response.CreateBulletinResponse;
import com.example.bulletin.application.service.characteristic.data.request.CreateCharacteristicRequest;
import com.example.bulletin.application.service.characteristic.data.response.CreateCharacteristicResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bulletin")
public class BulletinController {

    private final BulletinService service;

    @PostMapping
    public ResponseEntity<CreateBulletinResponse> createBulletin() throws BindException {
        return ResponseEntity.ok(service.createBulletin(new CreateBulletinRequest()));
    }
}
