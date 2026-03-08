package com.example.bulletin.application.statemachine.bulletin.action.helper;

import com.example.bulletin.application.data.request.BulletinRequest;
import com.example.bulletin.application.service.bulletin.data.response.UpdateBulletinResponse;
import com.example.bulletin.domain.entity.Bulletin;

public interface BulletinModifyService {
    void updateBulletin(Bulletin bulletin, BulletinRequest request);
}
