package com.example.chat.application.mapper;

import com.example.chat.application.data.response.ContactResponse;
import com.example.chat.domain.entity.ChatParticipant;
import com.example.chat.domain.entity.ChatRoom;
import com.example.chat.domain.entity.Contact;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.enums.ChatRoomType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ContactMapper {

    @Mapping(target = "ownerProfileId", source = "ownerProfile.id")
    @Mapping(target = "contactProfileId", source = "contactProfile.id")
    @Mapping(target = "chatId", expression = "java(findChatId(contact))")
    ContactResponse toResponse(Contact contact);

    default UUID findChatId(Contact contact) {
        Profile owner = contact.getOwnerProfile();
        Profile contactProfile = contact.getContactProfile();

        // Ищем чат, где оба профиля являются участниками
        return owner.getChatParticipants().stream()
                .map(ChatParticipant::getChatRoom)
                .filter(chatRoom -> chatRoom.getType() == ChatRoomType.TWO_PARTY)
                .filter(chatRoom ->
                        containsProfile(chatRoom, owner) &&
                                containsProfile(chatRoom, contactProfile)
                )
                .map(ChatRoom::getId)
                .findFirst()
                .orElse(null);
    }

    private boolean containsProfile(ChatRoom chatRoom, Profile profile) {
        return chatRoom.getParticipants().stream()
                .map(ChatParticipant::getProfile)
                .anyMatch(p -> p.getId().equals(profile.getId()));
    }

}