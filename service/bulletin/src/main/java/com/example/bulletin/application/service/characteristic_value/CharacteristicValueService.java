package com.example.bulletin.application.service.characteristic_value;

import com.example.bulletin.application.service.characteristic_value.data.request.*;
import com.example.bulletin.application.service.characteristic_value.data.response.*;

public interface CharacteristicValueService {
    GetCharacteristicValueResponse getCharacteristicValue(GetCharacteristicValueRequest request);
    GetCharacteristicValuesResponse getCharacteristicValues(GetCharacteristicValuesRequest request);
    CreateCharacteristicValueResponse createCharacteristicValue(CreateCharacteristicValueRequest request);
    RenameCharacteristicValueResponse renameCharacteristicValue(RenameCharacteristicValueRequest request);
    DeleteCharacteristicValueResponse deleteCharacteristicValue(DeleteCharacteristicValueRequest request);
}
