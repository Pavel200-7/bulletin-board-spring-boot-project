package com.example.bulletin.application.service.characteristic.data.response;

import com.example.bulletin.application.service.characteristic.data.response.data.CharacteristicResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RenameCharacteristicResponse {
    private CharacteristicResponse characteristicResponse;
}
