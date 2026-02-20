package com.example.bulletin.domain.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Embeddable
@AllArgsConstructor
public class Location {

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "town_name")
    private String townName;

    @Column(name = "location_name")
    private String locationName;

    protected Location() {}

}
