package com.example.bulletin.application.mapper;

import com.example.bulletin.application.service.user.data.response.data.UserResponse;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.domain.vo.UserData;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserData toData(User entity);
    UserResponse toResponse(User entity);
}