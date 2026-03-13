package com.example.chat.application.service.profile.data.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangeDescriptionRequest {
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}
