package com.example.bulletin.application.service.characteristic.data.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RenameCharacteristicRequest {
    @NotNull
    private UUID id;
    @NotBlank
    @Pattern(regexp = "^[ a-zA-Zа-яА-ЯёЁ0-9]+$",
            message = "Name must contain only letters (Russian or English) and digits")
    private String name;
}
