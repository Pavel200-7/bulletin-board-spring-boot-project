package com.example.chat.unit.domain.entity.chatroom.update;

import com.example.chat.domain.entity.ChatRoom;
import com.example.chat.domain.entity.Profile;
import com.example.chat.domain.entity.base.OwnerInfo;
import com.example.chat.domain.entity.base.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
public class ChatRoomUpdateTests {

    @Test
    public void shouldThrowWhenRenamingTwoPartyChat() {
        // Arrange
        ChatRoom chatRoom = createChatRoom();
        String newName = "New Chat Name";

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                chatRoom.rename(newName));

    }

    @Test
    public void shouldThrowWhenSettingImageToTwoPartyChat() {
        // Arrange
        ChatRoom chatRoom = createChatRoom();
        UUID imageId = UUID.randomUUID();

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                chatRoom.setImage(imageId));

    }

    private ChatRoom createChatRoom() {
        Profile creator = createProfile("creator@example.com", "Creator");
        Profile other = createProfile("other@example.com", "Other");

        creator.addContact(other);
        ChatRoom chatRoom = creator.getChatParticipants().getFirst().getChatRoom();
        return chatRoom;
    }

    private Profile createProfile(String email, String publicName) {
        User user = User.createUser(UUID.randomUUID(), email);
        OwnerInfo ownerInfo = new OwnerInfo(user);
        return Profile.createProfile(ownerInfo, publicName);
    }

}
