package com.example.bulletin.application.service.characteristic;

import com.example.bulletin.application.service.characteristic.data.request.*;
import com.example.bulletin.application.service.characteristic.data.response.*;

public interface CharacteristicService {
    GetCharacteristicResponse getCharacteristic(GetCharacteristicRequest request);
    GetCategoryCharacteristicsResponse getCategoryCharacteristics(GetCategoryCharacteristicsRequest request);
    CreateCharacteristicResponse createCharacteristic(CreateCharacteristicRequest request);
    RenameCharacteristicResponse renameCharacteristic(RenameCharacteristicRequest request);
    DeleteCharacteristicResponse deleteCharacteristic(DeleteCharacteristicRequest request);
}
