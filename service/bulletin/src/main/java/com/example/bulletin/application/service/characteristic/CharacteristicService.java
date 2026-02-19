package com.example.bulletin.application.service.characteristic;

import com.example.bulletin.application.service.characteristic.data.request.*;
import com.example.bulletin.application.service.characteristic.data.response.*;

public interface CharacteristicService {
    public GetCharacteristicResponse getCharacteristic(GetCharacteristicRequest request);
    public GetCategoryCharacteristicsResponse getCategoryCharacteristics(GetCategoryCharacteristicsRequest request);
    public CreateCharacteristicResponse createCharacteristic(CreateCharacteristicRequest request);
    public RenameCharacteristicResponse renameCharacteristic(RenameCharacteristicRequest request);
    public DeleteCharacteristicResponse deleteCharacteristic(DeleteCharacteristicRequest request);
}
