package com.example.bulletin.application.service.bulletin.data.request;

import com.example.bulletin.application.data.request.BulletinRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBulletinRequest {
    @Valid
    private BulletinRequest bulletinRequest;
}
