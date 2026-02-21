package com.example.bulletin.domain.entity;

import com.example.bulletin.domain.entity.base.BaseEntity;
import com.example.bulletin.domain.entity.base.Location;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Delegate;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "trade_accounts")
public class TradeAccount extends BaseEntity {

    @Id
    @Column(name = "id")
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Embedded
    @Delegate
    @Setter(AccessLevel.NONE)
    private OwnerInfo ownerInfo;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "contacts", columnDefinition = "TEXT")
    private String contacts;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Embedded
    @Setter(AccessLevel.NONE)
    private Location location;

    @Column(name = "coordinates_concrete")
    @Setter(AccessLevel.NONE)
    private boolean coordinatesExact ;

    @Column(name = "approved")
    @Setter(AccessLevel.NONE)
    private boolean approved;

    @Column(name = "image_id")
    private UUID imageId;

    protected TradeAccount() {}

    private TradeAccount(OwnerInfo ownerInfo) {
        this.id = UUID.randomUUID();
        this.ownerInfo = ownerInfo;
        this.approved = false;
    }

    public static TradeAccount createTradeAccount(OwnerInfo ownerInfo) {
        return new TradeAccount(ownerInfo);
    }

    public TradeAccount setApproximateLocation(Location location) {
        if (this.approved) {
            throw new IllegalStateException("Cannot set approximate location for approved account.");
        }

        this.location = location;
        this.coordinatesExact = false;
        return this;
    }

    public TradeAccount setExactLocation(Location location) {
        this.location = location;
        this.coordinatesExact = true;
        return this;
    }

    public TradeAccount approve() {
        validateForApproval();
        this.approved = true;
        return this;
    }

    private void validateForApproval() {
        if (this.approved) {
            throw new IllegalStateException("This account is already approved.");
        }
        if (isBlank(name)) {
            throw new IllegalStateException("Cannot approve account without name.");
        }
        if (isBlank(phone)) {
            throw new IllegalStateException("Cannot approve account without phone.");
        }
        if (!coordinatesExact) {
            throw new IllegalStateException("Cannot approve account without exact location data.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

}
