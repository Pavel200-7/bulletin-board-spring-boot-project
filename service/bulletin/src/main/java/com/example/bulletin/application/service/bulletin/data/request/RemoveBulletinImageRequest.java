package com.example.bulletin.application.service.bulletin.data.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RemoveBulletinImageRequest {
    private UUID bulletinId;
    private UUID imageId;
}
