package com.example.bulletin.application.mapper;

import com.example.bulletin.application.data.response.BulletinImageResponse;
import com.example.bulletin.domain.entity.BulletinImage;
import com.example.bulletin.domain.vo.BulletinImageData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BulletinImageMapper {

    @Mapping(target = "bulletinId", source = "bulletin.id")
    BulletinImageData toData(BulletinImage entity);

    @Mapping(target = "bulletinId", source = "bulletin.id")
    BulletinImageResponse toResponse(BulletinImage entity);
}
