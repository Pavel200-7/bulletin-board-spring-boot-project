package com.example.bulletin.application.service.characteristicvalue.data.response;

import com.example.bulletin.application.data.response.CharacteristicValueResponse;
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
