package com.example.bulletin.application.service.tradeaccount.data.response.data;

import lombok.Builder;
import lombok.Value;

import java.util.Objects;
import java.util.UUID;

@Value
@Builder
public class TradeAccountResponse {

    private UUID id;
    private UUID ownerId;
    private String name;
    private String phone;
    private String contacts;
    private String description;
    private Double latitude;
    private Double longitude;
    private String townName;
    private String locationName;
    private boolean coordinatesExact;
    private boolean approved;
    private UUID imageId;

    public boolean equalsData(TradeAccountResponse other) {
        if (other == null) return false;
        return Objects.equals(ownerId, other.ownerId) &&
                Objects.equals(name, other.name) &&
                Objects.equals(phone, other.phone) &&
                Objects.equals(contacts, other.contacts) &&
                Objects.equals(description, other.description) &&
                Objects.equals(latitude, other.latitude) &&
                Objects.equals(longitude, other.longitude) &&
                Objects.equals(townName, other.townName) &&
                Objects.equals(locationName, other.locationName) &&
                Objects.equals(coordinatesExact, other.coordinatesExact) &&
                Objects.equals(approved, other.approved) &&
                Objects.equals(imageId, other.imageId);
    }

}
