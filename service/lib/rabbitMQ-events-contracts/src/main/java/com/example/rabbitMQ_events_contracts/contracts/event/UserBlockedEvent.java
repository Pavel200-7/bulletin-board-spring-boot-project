package com.example.rabbitMQ_events_contracts.contracts.event;

import com.example.rabbitMQ_events_contracts.contracts.EventType;
import com.example.rabbitMQ_events_contracts.contracts.event.base.BaseEvent;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserBlockedEvent extends BaseEvent {

    @Override
    @JsonProperty("event_type")
    public EventType getEventType() {
        return EventType.USER_BLOCKED;
    }

    @JsonProperty("user_id")
    private String userId;
}
