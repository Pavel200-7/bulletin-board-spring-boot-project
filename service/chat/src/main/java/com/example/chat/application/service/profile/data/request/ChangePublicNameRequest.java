package com.example.chat.application.service.profile.data.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePublicNameRequest {
    @NotBlank(message = "Public name cannot be empty")
    @Pattern(regexp = "^[ a-zA-Zа-яА-ЯёЁ0-9]+$",
            message = "Public name must contain only letters (Russian or English) and digits")
    private String name;
}
