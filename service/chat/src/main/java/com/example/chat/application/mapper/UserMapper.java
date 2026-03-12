package com.example.chat.application.mapper;

import com.example.chat.application.data.response.UserResponse;
import com.example.chat.domain.entity.base.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserResponse toResponse(User entity);
}