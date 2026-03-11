package com.example.rabbitMQ_events_contracts.contracts.event.bulletin;

import com.example.rabbitMQ_events_contracts.contracts.EventType;
import com.example.rabbitMQ_events_contracts.contracts.event.base.BaseEvent;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class BulletinCompletedEvent extends BaseEvent {

    @Override
    @JsonProperty("event_type")
    public EventType getEventType() {
        return EventType.BULLETIN_COMPLETED;
    }

    @JsonProperty("publisher_id")
    private UUID bulletinId;

}
