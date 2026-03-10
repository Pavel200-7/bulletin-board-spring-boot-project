package com.example.bulletin.application.service.bulletin;

import com.example.bulletin.application.service.bulletin.data.request.*;
import com.example.bulletin.application.service.bulletin.data.response.*;

public interface BulletinService {
    GetBulletinResponse getBulletin(GetBulletinRequest request);
    GetBulletinPaginationResponse getBulletinPagination(GetBulletinPaginationRequest request);
    GetModifiableBulletinResponse getModifiableBulletin(GetModifiableBulletinRequest request);
    CreateBulletinResponse createBulletin(CreateBulletinRequest request) throws Exception;
    UpdateBulletinResponse updateBulletin(UpdateBulletinRequest request) throws Exception;
    AddBulletinImageResponse addImage(AddBulletinImageRequest request) throws Exception;
    RemoveBulletinImageResponse removeImage(RemoveBulletinImageRequest request) throws Exception;
    SetMainBulletinImageResponse setMainImage(SetMainBulletinImageRequest request) throws Exception;
    ApproveBulletinResponse approveBulletin(ApproveBulletinRequest request) throws Exception;
    PublishBulletinResponse publishBulletin(PublishBulletinRequest request) throws Exception;
    CloseBulletinResponse closeBulletin(CloseBulletinRequest request) throws Exception;
}
