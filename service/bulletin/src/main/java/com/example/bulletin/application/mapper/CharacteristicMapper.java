package com.example.bulletin.application.mapper;

import com.example.bulletin.application.service.characteristic.data.response.data.CharacteristicResponse;
import com.example.bulletin.domain.entity.Characteristic;
import com.example.bulletin.domain.vo.CharacteristicData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CharacteristicMapper {
    @Mapping(target = "categoryId", source = "category.id")
    CharacteristicData toData(Characteristic entity);

    @Mapping(target = "categoryId", source = "category.id")
    CharacteristicResponse toResponse(Characteristic entity);
}
