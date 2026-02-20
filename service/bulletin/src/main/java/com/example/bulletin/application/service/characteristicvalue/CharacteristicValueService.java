package com.example.bulletin.application.service.characteristicvalue;

import com.example.bulletin.application.service.characteristicvalue.data.request.*;
import com.example.bulletin.application.service.characteristicvalue.data.response.*;

public interface CharacteristicValueService {
    GetCharacteristicValueResponse getCharacteristicValue(GetCharacteristicValueRequest request);
    GetCharacteristicValuesResponse getCharacteristicValues(GetCharacteristicValuesRequest request);
    CreateCharacteristicValueResponse createCharacteristicValue(CreateCharacteristicValueRequest request);
    RenameCharacteristicValueResponse renameCharacteristicValue(RenameCharacteristicValueRequest request);
    DeleteCharacteristicValueResponse deleteCharacteristicValue(DeleteCharacteristicValueRequest request);
}
