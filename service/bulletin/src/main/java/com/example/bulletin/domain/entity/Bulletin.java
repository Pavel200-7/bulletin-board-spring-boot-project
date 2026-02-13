package com.example.bulletin.domain.entity;

import com.example.bulletin.domain.entity.base.BaseEntity;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Delegate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "bulletins")
public class Bulletin extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Embedded
    @Delegate
    private OwnerInfo ownerInfo;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "rating")
    private double rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Builder.Default
    @OneToMany(mappedBy = "bulletin", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<BulletinCharacteristic> characteristics = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "bulletin", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<BulletinImage> images = new ArrayList<>();

}
