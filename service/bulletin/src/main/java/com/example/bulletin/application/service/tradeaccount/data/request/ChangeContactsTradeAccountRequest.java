package com.example.bulletin.application.service.tradeaccount.data.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ChangeContactsTradeAccountRequest {
    @NotNull
    private UUID id;
    @NotBlank
    @Length(min = 0, max = 300)
    private String contacts;
}
