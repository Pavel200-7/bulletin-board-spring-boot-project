package com.example.bulletin.application.statemachine.bulletin.guard.helper;

import com.example.bulletin.domain.entity.Bulletin;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class BulletinApproveValidationDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Category is required")
    private UUID categoryId;

    @NotEmpty(message = "At least one characteristic is required")
    private List<CharacteristicValidationDto> characteristics;

    @NotEmpty(message = "At least one image is required")
    @Size(max = 10, message = "Cannot have more than 10 images")
    private List<ImageValidationDto> images;

    public static BulletinApproveValidationDto fromBulletin(Bulletin bulletin) {
        BulletinApproveValidationDto dto = new BulletinApproveValidationDto();
        dto.setTitle(bulletin.getTitle());
        dto.setDescription(bulletin.getDescription());
        dto.setCategoryId(bulletin.getCategory() != null ? bulletin.getCategory().getId() : null);
        dto.setCharacteristics(CharacteristicValidationDto.fromBulletinCharacteristics(bulletin.getCharacteristics()));
        dto.setImages(ImageValidationDto.fromBulletinImages(bulletin.getImages()));
        return dto;
    }

}
