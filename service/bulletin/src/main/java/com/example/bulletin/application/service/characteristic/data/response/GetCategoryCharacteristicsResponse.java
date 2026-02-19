package com.example.bulletin.application.service.characteristic.data.response;

import com.example.bulletin.application.service.characteristic.data.response.data.CharacteristicResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCategoryCharacteristicsResponse {
    private List<CharacteristicResponse> characteristicResponse;
}
