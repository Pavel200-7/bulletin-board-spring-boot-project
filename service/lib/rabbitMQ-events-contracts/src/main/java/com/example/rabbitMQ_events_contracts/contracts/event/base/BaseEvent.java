package com.example.rabbitMQ_events_contracts.contracts.event.base;

import com.example.rabbitMQ_events_contracts.contracts.EventType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEvent implements Serializable {
    @JsonProperty("event_id")
    private final String eventId = UUID.randomUUID().toString();

    @JsonProperty("timestamp")
    @Builder.Default
    private final Instant timestamp = Instant.now();

    @JsonProperty("event_type")
    public abstract EventType getEventType();
}
