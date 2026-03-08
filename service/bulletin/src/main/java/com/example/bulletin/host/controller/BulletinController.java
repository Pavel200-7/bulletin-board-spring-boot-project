package com.example.bulletin.host.controller;

import com.example.bulletin.application.service.bulletin.BulletinService;
import com.example.bulletin.application.service.bulletin.data.request.ApproveBulletinRequest;
import com.example.bulletin.application.service.bulletin.data.request.CloseBulletinRequest;
import com.example.bulletin.application.service.bulletin.data.request.CreateBulletinRequest;
import com.example.bulletin.application.service.bulletin.data.request.PublishBulletinRequest;
import com.example.bulletin.application.service.bulletin.data.response.ApproveBulletinResponse;
import com.example.bulletin.application.service.bulletin.data.response.CloseBulletinResponse;
import com.example.bulletin.application.service.bulletin.data.response.CreateBulletinResponse;
import com.example.bulletin.application.service.bulletin.data.response.PublishBulletinResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bulletin")
public class BulletinController {

    private final BulletinService service;

    @PostMapping
    public ResponseEntity<CreateBulletinResponse> createBulletin()
            throws BindException {
        return ResponseEntity.ok(service.createBulletin(new CreateBulletinRequest()));
    }

    @PutMapping("/approve")
    public ResponseEntity<ApproveBulletinResponse> approveBulletin(@Valid @RequestBody ApproveBulletinRequest request)
            throws BindException {
        return ResponseEntity.ok(service.approveBulletin(request));
    }

    @PutMapping("/publish")
    public ResponseEntity<PublishBulletinResponse> publishBulletin(@Valid @RequestBody PublishBulletinRequest request)
            throws BindException {
        return ResponseEntity.ok(service.publishBulletin(request));
    }
    @PutMapping("/close")
    public ResponseEntity<CloseBulletinResponse> closeBulletin(@Valid @RequestBody CloseBulletinRequest request)
            throws BindException {
        return ResponseEntity.ok(service.closeBulletin(request));
    }

}
