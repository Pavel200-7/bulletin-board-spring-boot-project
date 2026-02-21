package com.example.bulletin.application.mapper;

import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.vo.BulletinData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = {
                CategoryMapper.class,
                BulletinCharacteristicMapper.class,
                BulletinImageMapper.class})
public interface BulletinMapper {

    @Mapping(target = "ownerId", source = "ownerInfo.owner.id")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "characteristics", source = "characteristics")
    @Mapping(target = "images", source = "images")
    BulletinData toData(Bulletin entity);
}