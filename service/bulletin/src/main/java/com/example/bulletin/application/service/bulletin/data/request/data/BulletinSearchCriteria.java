package com.example.bulletin.application.service.bulletin.data.request.data;

import com.example.bulletin.application.service.bulletin.data.request.data.enums.BulletinOrderBy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort.Direction;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BulletinSearchCriteria {
    private String title;
    private double minPrice;
    private double maxPrice;
    private UUID categoryId;
    private List<UUID> characteristicValueIds;
    private UUID ownerId;

    private BulletinOrderBy orderBy;
    private Direction direction;
}
