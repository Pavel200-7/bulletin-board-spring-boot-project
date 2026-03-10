package com.example.bulletin.application.service.bulletin.data.response;

import com.example.bulletin.application.data.response.BulletinResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RemoveBulletinImageResponse {
    private BulletinResponse bulletinResponse;
}
