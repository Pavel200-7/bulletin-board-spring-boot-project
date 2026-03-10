package com.example.bulletin.application.service.tradeaccount.data.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RenameTradeAccountRequest {
    @NotBlank
    @Pattern(regexp = "^[ a-zA-Zа-яА-ЯёЁ0-9]+$",
            message = "Name must contain only letters (Russian or English) and digits")
    @Length(min = 3, max = 70)
    private String name;
}
