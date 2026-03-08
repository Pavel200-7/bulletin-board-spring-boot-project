package com.example.bulletin.application.service.bulletin.data.request;

import com.example.bulletin.application.data.request.BulletinRequest;
import com.example.bulletin.domain.entity.Bulletin;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateBulletinRequest {
    private BulletinRequest bulletinRequest;
}
