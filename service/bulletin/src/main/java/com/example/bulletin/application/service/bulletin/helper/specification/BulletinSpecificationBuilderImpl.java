package com.example.bulletin.application.service.bulletin.helper.specification;

import com.example.bulletin.application.service.bulletin.data.request.data.BulletinSearchCriteria;
import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.entity.BulletinCharacteristic;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class BulletinSpecificationBuilderImpl implements BulletinSpecificationBuilder {

    @Override
    public Specification<Bulletin> fromCriteria(BulletinSearchCriteria criteria) {
        List<Specification<Bulletin>> specs = new ArrayList<>();

        specs.add(this.isPublished());
        specs.add(titleContains(criteria.getTitle()));
        specs.add(priceBetween(criteria.getMinPrice(), criteria.getMaxPrice()));
        specs.add(hasCategory(criteria.getCategoryId()));
        specs.add(hasCharacteristicValues(criteria.getCharacteristicValueIds()));
        specs.add(hasOwner(criteria.getOwnerId()));

        return specs.stream()
                .reduce(Specification::and)
                .orElse(Specification.where(((root, query, criteriaBuilder) -> criteriaBuilder.conjunction())));
    }

    private Specification<Bulletin> hasCharacteristicValues(List<UUID> valueIds) {
        return (root, query, cb) -> {
            if (valueIds == null || valueIds.isEmpty()) {
                return cb.conjunction();
            }

            Join<Bulletin, BulletinCharacteristic> characteristics =
                    root.join("characteristics", JoinType.INNER);

            Path<UUID> valueIdPath = characteristics.get("value").get("id");
            return valueIdPath.in(valueIds);
        };
    }

    private Specification<Bulletin> priceBetween(Double min, Double max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return cb.conjunction();

            if (min == null) {
                return cb.lessThanOrEqualTo(root.get("price"), max);
            }
            if (max == null) {
                return cb.greaterThanOrEqualTo(root.get("price"), min);
            }
            return cb.between(root.get("price"), min, max);
        };
    }

    private Specification<Bulletin> titleContains(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("title")),
                    "%" + title.toLowerCase() + "%");
        };
    }

    private Specification<Bulletin> hasCategory(UUID categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) return cb.conjunction();
            return cb.equal(root.get("category").get("id"), categoryId);
        };
    }

    private Specification<Bulletin> hasOwner(UUID ownerId) {
        return (root, query, cb) -> {
            if (ownerId == null) return cb.conjunction();
            return cb.equal(root.get("ownerInfo").get("owner").get("id"), ownerId);
        };
    }

    private Specification<Bulletin> isPublished() {
        return (root, query, cb) ->
                cb.equal(root.get("state"), BulletinState.PUBLISHED);
    }

}
