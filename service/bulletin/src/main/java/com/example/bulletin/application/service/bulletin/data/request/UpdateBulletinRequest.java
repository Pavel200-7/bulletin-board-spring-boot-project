package com.example.bulletin.application.service.bulletin.data.request;

import com.example.bulletin.application.data.request.BulletinRequest;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateBulletinRequest {
    @Valid
    private BulletinRequest bulletinRequest;
}
