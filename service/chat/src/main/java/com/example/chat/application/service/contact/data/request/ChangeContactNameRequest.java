package com.example.chat.application.service.contact.data.request;

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
public class ChangeContactNameRequest {

    @NotNull(message = "Contact ID cannot be null")
    private UUID contactId;

    @NotBlank(message = "Contact name cannot be empty")
    private String newName;
}
