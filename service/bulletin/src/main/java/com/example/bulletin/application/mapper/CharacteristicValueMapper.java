package com.example.bulletin.application.mapper;

import com.example.bulletin.application.data.response.CharacteristicValueResponse;
import com.example.bulletin.domain.entity.CharacteristicValue;
import com.example.bulletin.domain.vo.CharacteristicValueData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CharacteristicValueMapper {

    @Mapping(target = "characteristicId", source = "characteristic.id")
    CharacteristicValueData toData(CharacteristicValue entity);

    @Mapping(target = "characteristicId", source = "characteristic.id")
    CharacteristicValueResponse toResponse(CharacteristicValue entity);
}
