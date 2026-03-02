package com.example.bulletin.application.service.characteristicvalue.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteCharacteristicValueResponse {
    @Builder.Default
    private boolean succeed = true;
}
