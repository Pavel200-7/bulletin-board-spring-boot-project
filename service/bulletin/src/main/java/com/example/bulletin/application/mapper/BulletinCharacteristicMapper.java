package com.example.bulletin.application.mapper;

import com.example.bulletin.domain.entity.BulletinCharacteristic;
import com.example.bulletin.domain.vo.BulletinCharacteristicData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
        CharacteristicMapper.class,
        CharacteristicValueMapper.class
})
public interface BulletinCharacteristicMapper {

    @Mapping(target = "bulletinId", source = "bulletin.id")
    public BulletinCharacteristicData toData(BulletinCharacteristic entity);
}
