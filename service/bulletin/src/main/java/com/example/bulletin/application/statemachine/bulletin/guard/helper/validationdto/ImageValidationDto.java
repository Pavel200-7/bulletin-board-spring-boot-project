package com.example.bulletin.application.statemachine.bulletin.guard.helper.validationdto;

import com.example.bulletin.domain.entity.BulletinImage;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageValidationDto {

    @NotNull(message = "Image ID is required")
    private UUID imageId;

    private boolean isMain;

    public static List<ImageValidationDto> fromBulletinImages(List<BulletinImage> images) {
        return images.stream()
                .map(img -> new ImageValidationDto(
                        img.getId(),
                        img.isMain()))
                .collect(Collectors.toList());
    }

}