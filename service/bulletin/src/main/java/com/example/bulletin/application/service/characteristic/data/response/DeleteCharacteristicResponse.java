package com.example.bulletin.application.service.characteristic.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteCharacteristicResponse {
    private boolean succeed = true;
}
