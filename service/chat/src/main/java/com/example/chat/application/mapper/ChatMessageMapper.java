package com.example.chat.application.mapper;

import com.example.chat.application.data.response.ChatMessageResponse;
import com.example.chat.domain.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatMessageMapper {

    @Mapping(target = "senderId", source = "sender.id")
    ChatMessageResponse toResponse(ChatMessage message);
}