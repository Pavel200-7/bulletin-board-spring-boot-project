package com.example.notification.application.mapper;

import com.example.notification.application.data.response.SubscriptionResponse;
import com.example.notification.domain.entity.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SubscriptionMapper {

    @Mapping(target = "ownerId", source = "ownerInfo.owner.id")
    SubscriptionResponse toResponse(Subscription entity);
}