package com.example.bulletin.application.mapper;

import com.example.bulletin.application.data.response.BulletinCharacteristicResponse;
import com.example.bulletin.domain.entity.BulletinCharacteristic;
import com.example.bulletin.domain.vo.BulletinCharacteristicData;
import org.mapstruct.*;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {
        CharacteristicMapper.class,
        CharacteristicValueMapper.class
})
public interface BulletinCharacteristicMapper {


    @Mapping(target = "bulletinId", source = "bulletin.id")
    public BulletinCharacteristicData toData(BulletinCharacteristic entity);

    @Mapping(target = "bulletinId", source = "bulletin.id")
    public BulletinCharacteristicResponse toResponse(BulletinCharacteristic entity);
}
