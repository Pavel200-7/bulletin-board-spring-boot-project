package com.example.bulletin.application.service.bulletin;

import com.example.bulletin.application.service.bulletin.data.request.CreateBulletinRequest;
import com.example.bulletin.application.service.bulletin.data.response.CreateBulletinResponse;
import org.springframework.validation.BindException;

public interface BulletinService {
    CreateBulletinResponse createBulletin(CreateBulletinRequest request) throws BindException;
}
