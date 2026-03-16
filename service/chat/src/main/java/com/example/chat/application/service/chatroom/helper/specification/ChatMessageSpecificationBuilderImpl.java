package com.example.chat.application.service.chatroom.helper.specification;

import com.example.chat.application.service.chatroom.helper.specification.data.MessageCursorCriteria;
import com.example.chat.domain.entity.ChatMessage;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ChatMessageSpecificationBuilderImpl implements ChatMessageSpecificationBuilder {

    @Override
    public Specification<ChatMessage> fromCursorCriteria(MessageCursorCriteria criteria) {
        List<Specification<ChatMessage>> specs = new ArrayList<>();

        specs.add(belongsToChatRoom(criteria.getChatRoomId()));
        if (criteria.getCreatedAt().isPresent()) {
            specs.add(applyCursor(criteria.getCreatedAt().get(), criteria.getDirection()));
        }
        specs.add(orderByDirection(criteria.getDirection()));

        return buildFromList(specs);
    }

    private Specification<ChatMessage> buildFromList(List<Specification<ChatMessage>> specs) {
        return specs.stream()
                .reduce(Specification::and)
                .orElse(Specification.where(((root, query, criteriaBuilder) -> criteriaBuilder.conjunction())));
    }

    private Specification<ChatMessage> belongsToChatRoom(UUID chatRoomId) {
        return (root, query, cb) ->
                cb.equal(root.get("chatRoom").get("id"), chatRoomId);
    }

    private Specification<ChatMessage> applyCursor(LocalDateTime cursorTime, Direction direction) {
        return (root, query, cb) -> {
            if (cursorTime == null) {
                return cb.conjunction();
            }

            if (direction == Direction.ASC) {
                return cb.greaterThan(root.get("createdAt"), cursorTime);
            } else {
                return cb.lessThan(root.get("createdAt"), cursorTime);
            }
        };
    }

    private Specification<ChatMessage> orderByDirection(Direction direction) {
        return (root, query, cb) -> {
            if (direction == Direction.ASC) {
                query.orderBy(cb.asc(root.get("createdAt")));
            } else {
                query.orderBy(cb.desc(root.get("createdAt")));
            }
            return cb.conjunction(); // always true
        };
    }

}
