package com.example.bulletin.domain.entity;

import com.example.bulletin.domain.entity.base.BaseEntity;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Delegate;

import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "trade_accounts")
public class TradeAccount extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Embedded
    @Delegate
    private OwnerInfo ownerInfo;

    @Column(name = "phone")
    private String phone;

    @Column(name = "contacts", columnDefinition = "TEXT")
    private String contacts;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "latitude", precision = 10, scale = 8)
    private Double latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private Double longitude;

    @Column(name = "is_coordinates_concrete")
    private boolean isCoordinatesConcrete;

    @Column(name = "is_approved")
    private boolean isApproved;

    @Column(name = "image_id", nullable = true)
    private UUID imageId;

}
