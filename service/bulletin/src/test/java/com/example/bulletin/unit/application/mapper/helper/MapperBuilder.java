package com.example.bulletin.unit.application.mapper.helper;

import com.example.bulletin.application.mapper.*;
import org.mapstruct.factory.Mappers;

public class MapperBuilder {

    private CharacteristicMapper characteristicMapper = Mappers.getMapper(
            CharacteristicMapper.class);
    private CharacteristicValueMapper characteristicValueMapper = Mappers.getMapper(
            CharacteristicValueMapper.class);
    private CategoryMapper categoryMapper = Mappers.getMapper(
            CategoryMapper.class);
    private BulletinImageMapper imageMapper = Mappers.getMapper(
            BulletinImageMapper.class);

    public BulletinCharacteristicMapper createBulletinCharacteristicMapper() {
        return new BulletinCharacteristicMapperImpl(characteristicMapper, characteristicValueMapper);
    }

    public BulletinMapper createBulletinMapper() {
        return new BulletinMapperImpl(categoryMapper,
                createBulletinCharacteristicMapper(),
                imageMapper);
    }

}
