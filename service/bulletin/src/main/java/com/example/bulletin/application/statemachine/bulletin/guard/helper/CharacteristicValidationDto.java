package com.example.bulletin.application.statemachine.bulletin.guard.helper;

import com.example.bulletin.domain.entity.BulletinCharacteristic;
import jakarta.validation.constraints.NotBlank;
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
public class CharacteristicValidationDto {

    @NotNull(message = "Characteristic ID is required")
    private UUID characteristicId;

    @NotNull(message = "Characteristic value is required")
    private UUID valueId;

    public static List<CharacteristicValidationDto> fromBulletinCharacteristics(
            List<BulletinCharacteristic> characteristics) {
        return characteristics.stream()
                .map(bc -> new CharacteristicValidationDto(
                        bc.getName().getId(),
                        bc.getValue() != null ? bc.getValue().getId() : null
                ))
                .collect(Collectors.toList());
    }

}