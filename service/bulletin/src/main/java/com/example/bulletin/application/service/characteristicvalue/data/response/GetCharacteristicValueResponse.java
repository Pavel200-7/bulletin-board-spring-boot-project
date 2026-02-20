package com.example.bulletin.application.service.characteristicvalue.data.response;

import com.example.bulletin.application.service.characteristicvalue.data.response.data.CharacteristicValueResponse;
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
