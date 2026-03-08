package com.example.bulletin.application.service.bulletin.data.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class PublishBulletinRequest {
    private UUID bulletinId;
}
