package com.example.bulletin.host.controller;

import com.example.bulletin.application.service.bulletin.BulletinService;
import com.example.bulletin.application.service.bulletin.data.request.*;
import com.example.bulletin.application.service.bulletin.data.response.*;
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
            throws Exception {
        return ResponseEntity.ok(service.createBulletin(new CreateBulletinRequest()));
    }

    @PutMapping
    public ResponseEntity<UpdateBulletinResponse> updateBulletin(@Valid @RequestBody UpdateBulletinRequest request)
            throws Exception {
        return ResponseEntity.ok(service.updateBulletin(request));
    }

    @PutMapping("/approve")
    public ResponseEntity<ApproveBulletinResponse> approveBulletin(@Valid @RequestBody ApproveBulletinRequest request)
            throws Exception {
        return ResponseEntity.ok(service.approveBulletin(request));
    }

    @PutMapping("/publish")
    public ResponseEntity<PublishBulletinResponse> publishBulletin(@Valid @RequestBody PublishBulletinRequest request)
            throws Exception {
        return ResponseEntity.ok(service.publishBulletin(request));
    }

    @PutMapping("/close")
    public ResponseEntity<CloseBulletinResponse> closeBulletin(@Valid @RequestBody CloseBulletinRequest request)
            throws Exception {
        return ResponseEntity.ok(service.closeBulletin(request));
    }

    @PutMapping("/add-image")
    public ResponseEntity<AddBulletinImageResponse> addImage(@Valid @RequestBody AddBulletinImageRequest request)
            throws Exception {
        return ResponseEntity.ok(service.addImage(request));
    }

    @PutMapping("/remove-image")
    public ResponseEntity<RemoveBulletinImageResponse> removeImage(@Valid @RequestBody RemoveBulletinImageRequest request)
            throws Exception {
        return ResponseEntity.ok(service.removeImage(request));
    }

    @PutMapping("/main-image")
    public ResponseEntity<SetMainBulletinImageResponse> setMainImage(@Valid @RequestBody SetMainBulletinImageRequest request)
            throws Exception {
        return ResponseEntity.ok(service.setMainImage(request));
    }

}
