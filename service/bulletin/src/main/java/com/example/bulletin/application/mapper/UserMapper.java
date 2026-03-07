package com.example.bulletin.application.mapper;

import com.example.bulletin.application.data.response.UserResponse;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.domain.vo.UserData;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserData toData(User entity);
    UserResponse toResponse(User entity);
}