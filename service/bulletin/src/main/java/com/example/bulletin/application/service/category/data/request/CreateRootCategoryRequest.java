package com.example.bulletin.application.service.category.data.request;

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
public class CreateRootCategoryRequest {
    @NotBlank
    @Pattern(regexp = "^[ a-zA-Zа-яА-ЯёЁ0-9]+$",
            message = "Name must contain only letters (Russian or English) and digits")
    private String name;
}
