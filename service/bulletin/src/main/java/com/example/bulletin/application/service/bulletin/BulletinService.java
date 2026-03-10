package com.example.bulletin.application.service.bulletin;

import com.example.bulletin.application.service.bulletin.data.request.*;
import com.example.bulletin.application.service.bulletin.data.response.*;
import org.springframework.validation.BindException;

public interface BulletinService {
    CreateBulletinResponse createBulletin(CreateBulletinRequest request) throws Exception;
    UpdateBulletinResponse updateBulletin(UpdateBulletinRequest request) throws Exception;
    ApproveBulletinResponse approveBulletin(ApproveBulletinRequest request) throws Exception;
    PublishBulletinResponse publishBulletin(PublishBulletinRequest request) throws Exception;
    CloseBulletinResponse closeBulletin(CloseBulletinRequest request) throws Exception;
}
