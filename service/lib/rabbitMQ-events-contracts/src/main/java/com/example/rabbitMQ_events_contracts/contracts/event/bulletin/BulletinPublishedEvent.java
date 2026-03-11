package com.example.rabbitMQ_events_contracts.contracts.event.bulletin;

import com.example.rabbitMQ_events_contracts.contracts.EventType;
import com.example.rabbitMQ_events_contracts.contracts.event.base.BaseEvent;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class BulletinPublishedEvent extends BaseEvent {

    @Override
    @JsonProperty("event_type")
    public EventType getEventType() {
        return EventType.BULLETIN_PUBLISHED;
    }

    @JsonProperty("publisher_id")
    private UUID publisherId;

    @JsonProperty("publisher_name")
    private String publisherName;

    @JsonProperty("publisher_id")
    private UUID bulletinId;

    @JsonProperty("bulletin_name")
    private String bulletinName;

    @JsonProperty("category_id")
    private UUID categoryId;

    @JsonProperty("characteristic_value_ids")
    private List<UUID> characteristicValueIds;

    @JsonProperty("price")
    private Double price;

}
