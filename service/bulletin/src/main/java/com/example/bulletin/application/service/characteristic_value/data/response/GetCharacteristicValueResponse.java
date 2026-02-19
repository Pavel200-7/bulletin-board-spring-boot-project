package com.example.bulletin.application.service.characteristic_value.data.response;

import com.example.bulletin.application.service.characteristic_value.data.response.data.CharacteristicValueResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCharacteristicValueResponse {
    private CharacteristicValueResponse characteristicValueResponse;
}
