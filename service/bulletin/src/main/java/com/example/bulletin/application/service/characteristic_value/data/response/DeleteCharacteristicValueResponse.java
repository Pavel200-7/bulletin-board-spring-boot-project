package com.example.bulletin.application.service.characteristic_value.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteCharacteristicValueResponse {
    private boolean succeed = true;
}
