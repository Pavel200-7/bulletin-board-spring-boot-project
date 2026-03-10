package com.example.bulletin.application.data.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
@ToString
@AllArgsConstructor
public class BulletinCharacteristicRequest {

    @NotNull(message = "Characteristic ID must not be null")
    private UUID characteristicId;

    @NotNull(message = "Characteristic value ID must not be null")
    private UUID characteristicValueId;
}
