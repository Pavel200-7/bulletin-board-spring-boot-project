package com.example.bulletin.application.service.bulletin.data.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SetMainBulletinImageRequest {
    @NotNull
    private UUID bulletinId;
    @NotNull
    private UUID imageId;
}
