package com.example.chat.application.service.chatroom.helper.event;

import com.example.chat.infrastructure.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarkMessagesAsReadHandler implements MessagesReadEventHandler{

    private final ChatMessageRepository messageRepository;

    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessagesRead(MessagesReadEvent event) {
        log.info("Обработка отметки прочитанными сообщений из chat room с id: {}, " +
                        "пользователем с Profile с id: {}, " +
                        "при последнем прочитанном сообщении с временной меткой: {}",
                event.getChatRoomId(), event.getReaderProfileId(), event.getLastReadMessageId());

        int updatedCount = messageRepository.markMessagesAsRead(
                event.getChatRoomId(),
                event.getReaderProfileId(),
                event.getLastReadMessageId()
        );

        log.info("{} Сообщений обозначено прочитанными.", updatedCount);
    }

}
