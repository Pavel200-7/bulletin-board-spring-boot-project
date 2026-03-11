package com.example.notification.application.mapper;

import com.example.notification.application.data.response.UserResponse;
import com.example.notification.domain.entity.base.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserResponse toResponse(User entity);
}