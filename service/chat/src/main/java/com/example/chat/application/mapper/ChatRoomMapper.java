package com.example.chat.application.mapper;

import com.example.chat.application.data.response.ChatRoomResponse;
import com.example.chat.domain.entity.ChatParticipant;
import com.example.chat.domain.entity.ChatRoom;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.enums.ChatRoomType;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {ChatParticipantMapper.class})
public interface ChatRoomMapper {

    @Mapping(target = "participantResponses", source = "chatRoom.participants")
    @Mapping(target = "imageId", expression = "java(findChatImageId(chatRoom, currentUserId))")
    ChatRoomResponse toResponseForTwoPartyRoom(ChatRoom chatRoom, UUID currentUserId);

    default UUID findChatImageId(ChatRoom chatRoom, UUID currentUserId) {
        if (chatRoom.getType() == ChatRoomType.TWO_PARTY) {
            return chatRoom.getParticipants().stream()
                    .map(ChatParticipant::getProfile)
                    .filter(profile -> !profile.getOwnerInfo().getOwnerId().equals(currentUserId))
                    .findFirst()
                    .map(Profile::getImageId)
                    .orElse(null);
        }
        return null;
    }

}