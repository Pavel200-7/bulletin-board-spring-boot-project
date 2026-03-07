package com.example.bulletin.application.mapper;

import com.example.bulletin.application.data.response.TradeAccountResponse;
import com.example.bulletin.domain.entity.TradeAccount;
import com.example.bulletin.domain.vo.TradeAccountData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TradeAccountMapper {

    @Mapping(target = "ownerId", source = "ownerInfo.owner.id")
    @Mapping(target = "latitude", source = "location.latitude")
    @Mapping(target = "longitude", source = "location.longitude")
    @Mapping(target = "townName", source = "location.townName")
    @Mapping(target = "locationName", source = "location.locationName")
    TradeAccountData toData(TradeAccount entity);

    @Mapping(target = "ownerId", source = "ownerInfo.owner.id")
    @Mapping(target = "latitude", source = "location.latitude")
    @Mapping(target = "longitude", source = "location.longitude")
    @Mapping(target = "townName", source = "location.townName")
    @Mapping(target = "locationName", source = "location.locationName")
    TradeAccountResponse toResponse(TradeAccount entity);
}
