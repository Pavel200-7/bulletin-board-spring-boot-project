package com.example.bulletin.application.service.bulletin.helper;

import com.example.bulletin.application.service.bulletin.data.request.UpdateBulletinRequest;
import com.example.bulletin.application.service.bulletin.data.response.UpdateBulletinResponse;
import com.example.bulletin.domain.entity.Bulletin;

public interface BulletinModifyService {
    UpdateBulletinResponse updateBulletin(Bulletin bulletin, UpdateBulletinRequest request);
}
