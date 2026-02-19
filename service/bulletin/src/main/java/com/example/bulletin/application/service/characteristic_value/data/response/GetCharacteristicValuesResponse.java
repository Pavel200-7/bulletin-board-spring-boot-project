package com.example.bulletin.application.service.characteristic_value.data.response;

import com.example.bulletin.application.service.characteristic_value.data.response.data.CharacteristicValueResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCharacteristicValuesResponse {
    private List<CharacteristicValueResponse> characteristicValueResponse;
}
