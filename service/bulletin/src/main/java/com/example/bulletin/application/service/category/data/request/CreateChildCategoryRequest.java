package com.example.bulletin.application.service.category.data.request;

import jakarta.validation.constraints.NotBlank;
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
public class CreateChildCategoryRequest {
    @NotNull
    private UUID parentId;
    @NotBlank
    private String name;
}
