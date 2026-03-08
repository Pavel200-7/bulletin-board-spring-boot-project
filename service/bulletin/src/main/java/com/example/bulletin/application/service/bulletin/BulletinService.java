package com.example.bulletin.application.service.bulletin;

import com.example.bulletin.application.service.bulletin.data.request.*;
import com.example.bulletin.application.service.bulletin.data.response.*;
import org.springframework.validation.BindException;

public interface BulletinService {
    CreateBulletinResponse createBulletin(CreateBulletinRequest request) throws BindException;
    UpdateBulletinResponse updateBulletin(UpdateBulletinRequest request);
    ApproveBulletinResponse approveBulletin(ApproveBulletinRequest request) throws BindException;
    PublishBulletinResponse publishBulletin(PublishBulletinRequest request) throws BindException;
    CloseBulletinResponse closeBulletin(CloseBulletinRequest request) throws BindException;
}
