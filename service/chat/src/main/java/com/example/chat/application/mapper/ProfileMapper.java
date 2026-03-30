package com.example.chat.application.mapper;

import com.example.chat.application.data.response.ProfileResponse;
import com.example.chat.application.service.profile.data.response.data.ProfilePaginationData;
import com.example.chat.domain.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProfileMapper {

    @Mapping(target = "ownerId", source = "ownerInfo.owner.id")
    ProfileResponse toResponse(Profile entity);

    @Mapping(target = "ownerId", expression = "java(entity.getOwnerInfo().getOwnerId())")
    @Mapping(target = "isContact", source = "isContact")
    ProfilePaginationData toPaginationData(Profile entity, boolean isContact);
}
