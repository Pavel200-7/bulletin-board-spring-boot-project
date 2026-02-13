package com.example.bulletin.domain.entity;

import com.example.bulletin.domain.entity.base.BaseEntity;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Delegate;

import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
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

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "coordinates_concrete")
    private boolean coordinatesConcrete;

    @Column(name = "approved")
    private boolean approved;

    @Column(name = "image_id", nullable = true)
    private UUID imageId;

}
