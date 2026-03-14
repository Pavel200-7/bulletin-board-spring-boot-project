package com.example.chat.application.mapper;

import com.example.chat.application.data.response.ChatParticipantResponse;
import com.example.chat.domain.entity.ChatParticipant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatParticipantMapper {

    @Mapping(target = "profileId", source = "profile.id")
    @Mapping(target = "chatRoomId", source = "chatRoom.id")
    ChatParticipantResponse toResponse(ChatParticipant participant);
}